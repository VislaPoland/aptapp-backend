package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.SlotUtils;
import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.dao.EventInviteDao;
import com.creatix.domain.dao.EventSlotDao;
import com.creatix.domain.dao.MaintenanceSlotDao;
import com.creatix.domain.dao.MaintenanceSlotScheduleDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.SlotDao;
import com.creatix.domain.dao.SlotUnitDao;
import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.property.slot.EventSlotDetailDto;
import com.creatix.domain.dto.property.slot.MaintenanceSlotDto;
import com.creatix.domain.dto.property.slot.PersistEventSlotRequest;
import com.creatix.domain.dto.property.slot.PersistMaintenanceSlotScheduleRequest;
import com.creatix.domain.dto.property.slot.ScheduledSlotsResponse;
import com.creatix.domain.dto.property.slot.SlotDto;
import com.creatix.domain.entity.store.EventInvite;
import com.creatix.domain.entity.store.EventSlot;
import com.creatix.domain.entity.store.MaintenanceSlot;
import com.creatix.domain.entity.store.MaintenanceSlotSchedule;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.Slot;
import com.creatix.domain.entity.store.SlotUnit;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.AudienceType;
import com.creatix.domain.enums.EventInviteResponse;
import com.creatix.domain.enums.ReservationStatus;
import com.creatix.message.template.push.EventNotificationTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.PushNotificationService;
import com.creatix.service.property.PropertyService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
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
    @Autowired
    private AccountService accountService;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private EventInviteDao eventInviteDao;

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
                    .map(keepOnlyPendingAndConfirmedReservations())
                    .collect(Collectors.toList()));
            if ( slots.size() > pageSize ) {
                result.setNextId(slots.get(pageSize).getId());
            }
        }
        else {
            result.setSlots(slots.stream()
                    .map(s -> mapper.toSlotDto(s))
                    .map(keepOnlyPendingAndConfirmedReservations())
                    .collect(Collectors.toList()));
        }

        return result;
    }

    private Function<SlotDto, SlotDto> keepOnlyPendingAndConfirmedReservations() {
        return s -> {
            if ( s instanceof MaintenanceSlotDto ) {
                MaintenanceSlotDto ms = (MaintenanceSlotDto) s;
                if ( ms.getReservations() != null ) {
                    ms.setReservations(ms.getReservations().stream()
                            .filter(r -> EnumSet.of(ReservationStatus.Pending, ReservationStatus.Confirmed).contains(r.getStatus()))
                            .collect(Collectors.toList()));
                }
            }

            return s;
        };
    }

    public EventSlot createEventSlot(@NotNull Long propertyId, @NotNull PersistEventSlotRequest request) throws IOException, TemplateException {
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

        final List<Account> recipients;
        if ( slot.getAudience() == AudienceType.Employees ) {
            recipients = accountService.getAccounts(new AccountRole[]{AccountRole.Maintenance, AccountRole.Security, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager}, propertyId);
        }
        else if ( slot.getAudience() == AudienceType.Tenants ) {
            recipients = accountService.getAccounts(new AccountRole[]{AccountRole.Tenant, AccountRole.SubTenant}, propertyId);
        }
        else if ( slot.getAudience() == AudienceType.Everyone ) {
            recipients = accountService.getAccounts(AccountRole.values(), propertyId);
        }
        else {
            recipients = Collections.emptyList();
        }

        Set<EventInvite> invites = new HashSet<>();
        for ( Account recipient : recipients ) {
            pushNotificationService.sendNotification(new EventNotificationTemplate(slot), recipient);
            final EventInvite invite = new EventInvite();
            invite.setAttendant(recipient);
            invite.setEvent(slot);
            invite.setResponse(EventInviteResponse.Invited);
            invites.add(invite);
            eventInviteDao.persist(invite);
        }
        slot.setInvites(invites);

        return slot;
    }

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }

    public EventSlot deleteEventSlotById(@NotNull Long slotId) {
        Objects.requireNonNull(slotId);

        final EventSlot slot = getOrElseThrow(slotId, eventSlotDao, new EntityNotFoundException(String.format("Slot id=%d not found", slotId)));

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

        return eventSlotDao.findByPropertyIdAndAccountAndStartBetween(propertyId, authorizationManager.getCurrentAccount(), beginDt, endDt);
    }

    public EventSlotDetailDto getEventDetailWithFilteredAttendants(@NotNull Long slotId, String filter) {
        Objects.requireNonNull(slotId);

        final EventSlot eventSlot = getOrElseThrow(slotId, eventSlotDao, new EntityNotFoundException(String.format("Slot id=%d not found", slotId)));

        final Account account = authorizationManager.getCurrentAccount();
        if ( !AccountRole.PropertyManager.equals(account.getRole())
                && !AccountRole.AssistantPropertyManager.equals(account.getRole())
                && !eventInviteDao.isUserInvitedToEvent(slotId, account) ) {
            throw new SecurityException("Not allowed to read event detail without invitation");
        }

        List<EventInvite> invites = eventInviteDao.findByEventSlotIdFilterByAttendantNameOrderByAttendantFirstNameAsc(slotId, filter);

        final EventSlotDetailDto detailDto = mapper.toEventSlotDetailDto(eventSlot);
        detailDto.setResponses(invites.stream()
                .collect(Collectors.groupingBy(EventInvite::getResponse)).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .map(i -> mapper.toEventSlotDetailAccountDto(i.getAttendant()))
                        .collect(Collectors.toList()))));

        return detailDto;
    }

    public void respondToEventInvite(@NotNull Long slotId, @NotNull EventInviteResponse response) {
        Objects.requireNonNull(slotId);
        Objects.requireNonNull(response);

        final EventSlot eventSlot = getOrElseThrow(slotId, eventSlotDao, new EntityNotFoundException(String.format("Slot id=%d not found", slotId)));
        final Account account = authorizationManager.getCurrentAccount();

        if ( !eventInviteDao.isUserInvitedToEvent(slotId, account) ) {
            throw new SecurityException("Not allowed to respond to an event invitation without the existing invitation");
        }

        final EventInvite invite = eventInviteDao.findBySlotIdAndAccount(slotId, account);
        invite.setResponse(response);
        eventInviteDao.persist(invite);
    }

    private Slot createMaintenanceSlotFromSchedule(@NotNull LocalDate date, @NotNull MaintenanceSlotSchedule schedule) {
        Objects.requireNonNull(date, "Date is null");
        Objects.requireNonNull(schedule, "Schedule is null");


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

        for ( int idx = 0, unitCount = SlotUtils.calculateUnitCount(beginDt, endDt, schedule.getUnitDurationMinutes()); idx < unitCount; ++idx ) {
            final SlotUnit unit = new SlotUnit();
            unit.setSlot(slot);
            unit.setCapacity(schedule.getInitialCapacity());
            unit.setInitialCapacity(schedule.getInitialCapacity());
            unit.setOffset(idx);
            slotUnitDao.persist(unit);

            slot.addUnit(unit);
        }

        schedule.addSlot(slot);

        return slot;
    }

    @RoleSecured
    public List<MaintenanceSlot> getMaintenanceSlotsByPropertyAndDay(long propertyId, LocalDate day) {
        final Property property = propertyService.getProperty(propertyId);
        final OffsetDateTime beginDt = day.atStartOfDay(property.getZoneOffset(day.atStartOfDay())).toOffsetDateTime();
        final OffsetDateTime endDt = beginDt.withHour(23).withMinute(59).withSecond(59);
        return maintenanceSlotDao.findByPropertyIdAndStartBetween(propertyId, beginDt, endDt);
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public MaintenanceSlotSchedule createSchedule(long propertyId, PersistMaintenanceSlotScheduleRequest request) throws SecurityException {
        final Property property = propertyDao.findById(propertyId);
        if ( authorizationManager.isManager(property) || authorizationManager.isOwner(property) ) {

            final boolean releasePreviousSchedule = (property.getSchedule() != null);

            if ( releasePreviousSchedule ) {
                releaseScheduleSlots(property.getSchedule());
            }

            final MaintenanceSlotSchedule schedule = releasePreviousSchedule ? property.getSchedule() : new MaintenanceSlotSchedule();
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

    private void releaseScheduleSlots(MaintenanceSlotSchedule schedule) {
        final ArrayList<MaintenanceSlot> slots = new ArrayList<>(schedule.getSlots());
        slots.forEach(this::releaseSlot);
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

            for ( LocalDate dStop = d.plusMonths(4); d.isBefore(dStop); d = d.plusWeeks(1) ) {
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
        oldSlots.forEach(this::releaseSlot);
    }

    private void releaseSlot(@NotNull MaintenanceSlot slot) {
        Objects.requireNonNull(slot, "Slot is null");

        if ( slot.getUnits() != null ) {
            final ArrayList<SlotUnit> slotUnits = new ArrayList<>(slot.getUnits());
            slotUnits.forEach(u -> {
                if ( (u.getReservations() == null) || u.getReservations().isEmpty() ) {
                    slot.removeUnit(u);
                    slotUnitDao.delete(u);
                }
            });
            if ( slot.getUnits().isEmpty() ) {
                // remove empty slot
                final MaintenanceSlotSchedule schedule = slot.getSchedule();
                if ( schedule != null ) {
                    schedule.removeSlot(slot);
                }
                slotDao.delete(slot);
            }
            else {
                // dissociate slot with schedule
                final MaintenanceSlotSchedule schedule = slot.getSchedule();
                if ( schedule != null ) {
                    schedule.removeSlot(slot);
                    slotDao.persist(slot);
                }
            }
        }
    }

}
