package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.property.slot.PersistMaintenanceSlotScheduleRequest;
import com.creatix.domain.dto.property.slot.PersistEventSlotRequest;
import com.creatix.domain.dto.property.slot.ScheduledSlotsResponse;
import com.creatix.domain.entity.store.*;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.property.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.time.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class SlotService {

    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private SlotUnitDao slotUnitDao;
    @Autowired
    private MaintenanceSlotScheduleDao maintenanceSlotScheduleDao;
    @Autowired
    private MaintenanceSlotDao maintenanceSlotDao;
    @Autowired
    private EventSlotDao eventSlotDao;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private SlotDao slotDao;
    @Autowired
    private Mapper mapper;

    public ScheduledSlotsResponse getSlotsByFilter(@NotNull Long propertyId, LocalDate beginDate, LocalDate endDate, Long startId, Integer pageSize) {
        Objects.requireNonNull(propertyId, "Property id is required");

        final Property property = propertyService.getProperty(propertyId);

        final List<Slot> slots;

        if ( (beginDate != null) && (endDate != null) ) {
            final OffsetDateTime beginDt = beginDate.atStartOfDay(property.getZoneOffset(beginDate.atStartOfDay())).toOffsetDateTime();
            final OffsetDateTime endDt = endDate.atStartOfDay(property.getZoneOffset(endDate.atStartOfDay())).toOffsetDateTime()
                    .withHour(23).withMinute(59).withSecond(59);
            slots = slotDao.findByPropertyAndAccountAndDateRange(property, authorizationManager.getCurrentAccount(), beginDt, endDt);
        }
        else if ( (startId != null) && (pageSize != null) ) {
            final Slot slot = slotDao.findById(startId);
            if ( slot == null ) {
                throw new EntityNotFoundException(String.format("Slot id=%d not found", startId));
            }

            slots = slotDao.findByPropertyAndAccountAndSlotIdGreaterOrEqual(property, authorizationManager.getCurrentAccount(), slot.getId(), pageSize + 1);
        }
        else {
            if ( pageSize == null ) {
                throw new IllegalArgumentException("Page size is required");
            }
            slots = slotDao.findByPropertyAndAccountAndBeginTime(property, authorizationManager.getCurrentAccount(), OffsetDateTime.now(), pageSize + 1);
        }

        final ScheduledSlotsResponse result = new ScheduledSlotsResponse();
        if ( pageSize != null ) {
            result.setSlots(slots.stream()
                    .limit(pageSize)
                    .map(s -> mapper.toSlotDto(s))
                    .collect(Collectors.toList()));
            if ( slots.size() > pageSize ) {
                result.setNextId(slots.get(pageSize).getId());
            }
        }
        else {
            result.setSlots(slots.stream()
                    .map(s -> mapper.toSlotDto(s))
                    .collect(Collectors.toList()));
        }

        return result;
    }

    public EventSlot createEventSlot(@NotNull Long propertyId, @NotNull PersistEventSlotRequest request) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(request);

        final Property property = propertyService.getProperty(propertyId);
        final EventSlot slot = new EventSlot();
        mapper.fillEventSlot(request, slot);
        slot.setEndTime(slot.getBeginTime().plusMinutes(request.getDurationMinutes()));
        slot.setProperty(property);

        final SlotUnit unit = new SlotUnit();
        unit.setCapacity(request.getInitialCapacity());
        unit.setInitialCapacity(request.getInitialCapacity());
        unit.setOffset(0);
        slot.addUnit(unit);

        eventSlotDao.persist(slot);

        return slot;
    }

    public EventSlot deleteEventSlotById(@NotNull Long slotId) {
        Objects.requireNonNull(slotId);

        final EventSlot slot = eventSlotDao.findById(slotId);
        if ( slot == null ) {
            throw new EntityNotFoundException(String.format("Slot id=%d nto found", slotId));
        }

        eventSlotDao.delete(slot);

        return slot;
    }

    public List<EventSlot> getEventSlotsByPropertyIdAndTimeRange(@NotNull Long propertyId, @NotNull LocalDate beginDate, @NotNull LocalDate endDate) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(beginDate);
        Objects.requireNonNull(endDate);

        final Property property = propertyService.getProperty(propertyId);

        final OffsetDateTime beginDt = beginDate.atStartOfDay().atOffset(property.getZoneOffset(beginDate.atStartOfDay()));
        final OffsetDateTime endDt = endDate.atTime(23, 59, 59).atOffset(property.getZoneOffset(endDate.atTime(23, 59, 59)));

        return eventSlotDao.findByPropertyIdAndStartBetween(propertyId, beginDt, endDt);
    }

    private Slot createMaintenanceSlotFromSchedule(@NotNull LocalDate date, @NotNull MaintenanceSlotSchedule schedule) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(schedule);


        final OffsetDateTime beginDt = date.atTime(schedule.getBeginTime()).atOffset(schedule.getZoneOffset(date.atTime(schedule.getBeginTime())));
        final OffsetDateTime endDt = date.atTime(schedule.getEndTime()).atOffset(schedule.getZoneOffset(date.atTime(schedule.getEndTime())));

        // create parent slot
        final MaintenanceSlot slot = new MaintenanceSlot();
        slot.setSchedule(schedule);
        slot.setBeginTime(beginDt);
        slot.setEndTime(endDt);
        slot.setUnitDurationMinutes(schedule.getUnitDurationMinutes());
        slot.setProperty(schedule.getProperty());
        maintenanceSlotDao.persist(slot);

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

    private int calculateUnitCount(OffsetDateTime beginDt, OffsetDateTime endDt, int unitDurationMinutes) {
        final Duration slotDuration = Duration.between(beginDt, endDt);
        return (int) (slotDuration.toMinutes() / unitDurationMinutes);
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

    @RoleSecured
    public List<MaintenanceSlot> getMaintenanceSlotsByPropertyAndDay(long propertyId, LocalDate day) {
        final Property property = propertyService.getProperty(propertyId);
        final OffsetDateTime beginDt = day.atStartOfDay(property.getZoneOffset(day.atStartOfDay())).toOffsetDateTime();
        final OffsetDateTime endDt = beginDt.withHour(23).withMinute(59).withSecond(59);
        return maintenanceSlotDao.findByPropertyIdAndStartBetween(propertyId, beginDt, endDt);
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.PropertyOwner})
    public MaintenanceSlotSchedule createSchedule(long propertyId, PersistMaintenanceSlotScheduleRequest request) throws SecurityException {
        final Property property = propertyDao.findById(propertyId);
        if ( authorizationManager.isManager(property) || authorizationManager.isOwner(property) ) {

            final MaintenanceSlotSchedule schedule = property.getSchedule() != null ? property.getSchedule() : new MaintenanceSlotSchedule();
            schedule.setBeginTime(request.getBeginTime());
            schedule.setEndTime(request.getEndTime());
            schedule.setDaysOfWeek(request.getDaysOfWeek());
            schedule.setProperty(property);
            schedule.setTimeZone(property.getTimeZone());
            schedule.setInitialCapacity(request.getInitialCapacity());
            schedule.setUnitDurationMinutes(request.getUnitDurationMinutes());
            maintenanceSlotScheduleDao.persist(schedule);
            property.setSchedule(schedule);
            propertyDao.persist(property);

            scheduleSlots(schedule);

            return schedule;
        }
        else {
            throw new SecurityException("Not allowed to modify property schedule");
        }
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public MaintenanceSlotSchedule deleteScheduleById(long slotScheduleId) {
        final MaintenanceSlotSchedule schedule = maintenanceSlotScheduleDao.findById(slotScheduleId);
        if ( schedule == null ) {
            throw new EntityNotFoundException(String.format("slot schedule id=%d not found", slotScheduleId));
        }
        authorizationManager.checkManager(schedule.getProperty());

        final List<MaintenanceSlot> slots = maintenanceSlotDao.findByScheduleAndStartAfter(schedule, OffsetDateTime.now());
        for ( MaintenanceSlot slot : slots ) {
            if ( (slot.getReservations() == null) || slot.getReservations().isEmpty() ) {
                maintenanceSlotDao.delete(slot);
            }
            else {
                // will not delete slot that has reservations on it, just unlink it from slot schedule
                slot.setSchedule(null);
                maintenanceSlotDao.persist(slot);
            }
        }
        maintenanceSlotScheduleDao.delete(schedule);
        return schedule;
    }

    @Scheduled(cron = "0 0 * * * *") // the top of every hour of every day
    public void scheduleSlots() {
        final List<MaintenanceSlotSchedule> schedules = maintenanceSlotScheduleDao.findAll();
        schedules.forEach(this::scheduleSlots);
    }

    private void scheduleSlots(MaintenanceSlotSchedule schedule) {
        if ( (schedule == null) || (schedule.getDaysOfWeek() == null) || schedule.getDaysOfWeek().isEmpty() ) {
            return;
        }

        final List<MaintenanceSlot> scheduledSlots = maintenanceSlotDao.findByScheduleAndStartAfter(schedule, OffsetDateTime.now().minusDays(1));
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

                createMaintenanceSlotFromSchedule(dt.toLocalDate(), schedule);
            }
        }
    }


    @Scheduled(cron = "0 10 */6 * * *") // every 6 hours, 10 minutes past full hour
    public void cleanupOldSlots() {
        final List<MaintenanceSlot> oldSlots = maintenanceSlotDao.findByEndTimeBefore(OffsetDateTime.now().minusWeeks(1));
        oldSlots.forEach(slot -> {
            if ( slot.getReservations().isEmpty() ) {
                slotDao.delete(slot);
            }
        });
    }

}
