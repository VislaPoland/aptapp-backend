package com.creatix.service;

import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.SlotDao;
import com.creatix.domain.dao.SlotScheduleDao;
import com.creatix.domain.dao.SlotUnitDao;
import com.creatix.domain.dto.property.slot.PersistSlotRequest;
import com.creatix.domain.dto.property.slot.PersistSlotScheduleRequest;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.Slot;
import com.creatix.domain.entity.SlotSchedule;
import com.creatix.domain.entity.SlotUnit;
import com.creatix.security.AuthorizationManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class SlotService {

    private static final int SLOT_UNIT_DURATION_MINUTES = 30;

    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private SlotDao slotDao;
    @Autowired
    private SlotUnitDao slotUnitDao;
    @Autowired
    private SlotScheduleDao slotScheduleDao;


    private Slot createSlotFromSchedule(@NotNull LocalDate date, @NotNull SlotSchedule schedule) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(schedule);

        if ( schedule.getBeginTime().isBefore(LocalTime.now()) ) {
            throw new IllegalArgumentException("Cannot create slot in the past");
        }

        final OffsetDateTime beginDt = date.atTime(schedule.getBeginTime()).atOffset(schedule.getZoneOffset(date.atTime(schedule.getBeginTime())));
        final OffsetDateTime endDt = date.atTime(schedule.getEndTime()).atOffset(schedule.getZoneOffset(date.atTime(schedule.getEndTime())));

        // create parent slot
        final Slot slot = new Slot();
        slot.setBeginTime(beginDt);
        slot.setEndTime(endDt);
        slot.setUnitDurationMinutes(schedule.getUnitDurationMinutes());
        slot.setProperty(schedule.getProperty());
        slot.setTargetRole(schedule.getTargetRole());
        slotDao.persist(slot);

        // create slot units

        for ( int idx = 0, unitCount = calculateUnitCount(beginDt, endDt, schedule.getUnitDurationMinutes()); idx < unitCount; ++idx ) {
            final SlotUnit unit = new SlotUnit();
            unit.setSlot(slot);
            unit.setCapacity(schedule.getInitialCapacity());
            unit.setInitialCapacity(schedule.getInitialCapacity());
            unit.setOffset(idx);
            slotUnitDao.persist(unit);

            slot.addUnit(unit);
        }

        return slot;
    }

    int calculateUnitCount(OffsetDateTime beginDt, OffsetDateTime endDt, int unitDurationMinutes) {
        final Duration slotDuration = Duration.between(beginDt, endDt);
        return (int) (slotDuration.get(ChronoUnit.MINUTES) / unitDurationMinutes);
    }

    int calculateUnitCount(int durationMinutes, int unitDurationMinutes) {
        return (durationMinutes / unitDurationMinutes);
    }

    int calculateUnitOffset(Slot slot, OffsetDateTime unitTime) {
        final Duration unitOffset = Duration.between(slot.getBeginTime(), unitTime);
        final long offsetMinutes = unitOffset.toMinutes();
        if ( offsetMinutes < 0 ) {
            throw new IllegalArgumentException("Time is before slot begin time");
        }

        return (int) (offsetMinutes / slot.getUnitDurationMinutes());
    }

    public Slot deleteById(long slotId) {
        final Slot slot = slotDao.findById(slotId);
        if ( slot == null ) {
            throw new EntityNotFoundException(String.format("Slot id=%d not found", slotId));
        }
        if ( slot.getBeginTime().isBefore(OffsetDateTime.now()) ) {
            throw new IllegalArgumentException("Cannot delete slot in past");
        }

        // check if slot unit is not reserved
        if ( (slot.getReservations() != null) && !slot.getReservations().isEmpty() ) {
            throw new IllegalStateException("Cannot delete slot that has reservation(s)");
        }

        slotDao.delete(slot);

        return slot;
    }

    public SlotUnit deleteUnitById(long slotUnitId) {
        final SlotUnit slotUnit = slotUnitDao.findById(slotUnitId);
        if ( slotUnit == null ) {
            throw new EntityNotFoundException(String.format("Slot unit id=%d not found", slotUnitId));
        }
        if ( slotUnit.getSlot().getBeginTime().isBefore(OffsetDateTime.now()) ) {
            throw new IllegalArgumentException("Cannot delete slot unit when slot starts in past");
        }
        if ( (slotUnit.getReservations() != null) && !slotUnit.getReservations().isEmpty() ) {
            throw new IllegalStateException("Cannot delete slot unit that has reservation(s)");
        }

        slotUnitDao.delete(slotUnit);

        return slotUnit;
    }

    public List<Slot> getByPropertyAndDateRange(long propertyId, OffsetDateTime beginDt, OffsetDateTime endDt) {
        return slotDao.findByPropertyIdAndStartBetween(propertyId, beginDt, endDt);
    }

    private static void validateSlotDuration(int durationMinutes) {
        if ( (durationMinutes % SLOT_UNIT_DURATION_MINUTES) != 0 ) {
            throw new IllegalArgumentException("Slot duration must be multiply of " + SLOT_UNIT_DURATION_MINUTES);
        }
    }

    private static Date computeEndTime(PersistSlotRequest request) {
        DateTime endDt = new DateTime(request.getBeginTime());
        endDt = endDt.plusMinutes(request.getDurationMinutes());
        return endDt.toDate();
    }

    public SlotSchedule createSchedule(long gymId, PersistSlotScheduleRequest request) throws ParseException {
        final Property property = propertyDao.findById(gymId);
        authorizationManager.isManager(property);

        final SlotSchedule schedule = new SlotSchedule();
        schedule.setBeginTime(request.getBeginTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setDaysOfWeek(request.getDaysOfWeek());
        schedule.setProperty(property);
        schedule.setTimeZone(schedule.getProperty().getTimeZone());
        schedule.setInitialCapacity(schedule.getInitialCapacity());
        schedule.setUnitDurationMinutes(schedule.getUnitDurationMinutes());
        schedule.setTargetRole(schedule.getTargetRole());
        slotScheduleDao.persist(schedule);

        scheduleSlots(schedule);

        return schedule;
    }

    public SlotSchedule deleteScheduleById(long slotScheduleId) {
        final SlotSchedule schedule = slotScheduleDao.findById(slotScheduleId);
        if ( schedule == null ) {
            throw new EntityNotFoundException(String.format("slot schedule id=%d not found", slotScheduleId));
        }
        authorizationManager.checkManager(schedule.getProperty());

        final List<Slot> slots = slotDao.findByScheduleAndStartAfter(schedule, OffsetDateTime.now());
        for ( Slot slot : slots ) {
            if ( (slot.getReservations() == null) || slot.getReservations().isEmpty() ) {
                slotDao.delete(slot);
            }
            else {
                // will not delete slot that has reservations on it, just unlink it from slot schedule
                slot.setSchedule(null);
                slotDao.persist(slot);
            }
        }
        slotScheduleDao.delete(schedule);
        return schedule;
    }

    public List<SlotSchedule> getSlotSchedulesByPropertyId(long propertyId) {
        final Property property = propertyDao.findById(propertyId);
        authorizationManager.isManager(property);

        return slotScheduleDao.findByProperty(property);
    }

    @Scheduled(cron = "0 0 * * * *") // the top of every hour of every day
    public void scheduleSlots() {
        final List<SlotSchedule> schedules = slotScheduleDao.findAll();
        schedules.forEach(this::scheduleSlots);
    }

    private void scheduleSlots(SlotSchedule schedule) {
        if ( (schedule == null) || (schedule.getDaysOfWeek() == null) || schedule.getDaysOfWeek().isEmpty() ) {
            return;
        }

        final List<Slot> scheduledSlots = slotDao.findByScheduleAndStartAfter(schedule, OffsetDateTime.now());
        final Set<ZonedDateTime> scheduledTimes = scheduledSlots.stream()
                .map(Slot::getBeginTime)
                .map(dt -> dt.atZoneSameInstant(ZoneId.systemDefault()))
                .collect(Collectors.toSet());

        for ( DayOfWeek dayOfWeek : schedule.getDaysOfWeek() ) {
            LocalDate d = LocalDate.now().with(dayOfWeek);

            for ( LocalDate dStop = d.plusMonths(2); d.isBefore(dStop); d = d.plusWeeks(1) ) {
                if ( d.isBefore(LocalDate.now()) ) {
                    continue;
                }

                final OffsetDateTime dt = d
                        .atTime(schedule.getBeginTime())
                        .atOffset(schedule.getZoneOffset(d.atTime(schedule.getBeginTime())));

                if ( scheduledTimes.contains(dt.atZoneSameInstant(ZoneId.systemDefault())) ) {
                    continue;
                }

                createSlotFromSchedule(dt.toLocalDate(), schedule);
            }
        }
    }

}
