package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.SlotUtils;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.property.slot.*;
import com.creatix.domain.entity.store.*;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.attachment.EventPhoto;
import com.creatix.domain.entity.store.notification.EventInviteNotification;
import com.creatix.domain.entity.store.notification.NotificationGroup;
import com.creatix.domain.enums.*;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.push.EventNotificationAdjustTemplate;
import com.creatix.message.template.push.EventNotificationTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.PushNotificationSender;
import com.creatix.service.property.PropertyService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class SlotService {

    private static final Logger logger = LoggerFactory.getLogger(SlotService.class);

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
    private PushNotificationSender pushNotificationSender;
    @Autowired
    private SmsMessageSender smsMessageSender;
    @Autowired
    private EventInviteDao eventInviteDao;
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private NotificationGroupDao notificationGroupDao;
    @Autowired
    private AttachmentService attachmentService;

    @Nonnull
    public ScheduledSlotsResponse getSlotsByFilter(@Nonnull Long propertyId, @Nullable LocalDate beginDate, @Nullable LocalDate endDate, @Nullable Long startId, @Nullable Integer pageSize) {
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

    public EventSlot updateEventSlot(@Nonnull Long eventSlotId, @Nonnull UpdateEventSlotRequest data) throws IOException, TemplateException {
        Objects.requireNonNull(eventSlotId);
        Objects.requireNonNull(data);

        final EventSlot eventSlot = getOrElseThrow(eventSlotId, eventSlotDao, new EntityNotFoundException(String.format("Event slot not found, slot_id=%d", eventSlotId)));
        if ( data.getAudience() != null ) {
            eventSlot.setAudience(data.getAudience());
        }
        if ( data.getDescription() != null ) {
            eventSlot.setDescription(data.getDescription());
        }
        if ( data.getTitle() != null ) {
            eventSlot.setTitle(data.getTitle());
        }
        if ( data.getLocation() != null ) {
            eventSlot.setLocation(data.getLocation());
        }
        if ( data.getBeginTime() != null ) {
            eventSlot.setBeginTime(data.getBeginTime());
        }
        if ( data.getUnitDurationMinutes() != null ) {
            eventSlot.setUnitDurationMinutes(data.getUnitDurationMinutes());
        }
        // update end time just in case that begin_time or unit_duration_minutes was changed
        eventSlot.setEndTime(eventSlot.getBeginTime().plusMinutes(eventSlot.getUnitDurationMinutes()));

        eventSlotDao.persist(eventSlot);


        final NotificationGroup notificationGroup = new NotificationGroup();
        notificationGroupDao.persist(notificationGroup);

        for ( final Account attendant : getEventAttendants(eventSlot) ) {
            // notify attendant by push notification
            pushNotificationSender.sendNotification(new EventNotificationAdjustTemplate(eventSlot), attendant);
        }

        return eventSlot;
    }
    
    public EventSlot createEventSlot(@Nonnull Long propertyId, @Nonnull PersistEventSlotRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(request);

        final Property property = propertyService.getProperty(propertyId);
        final EventSlot slot = new EventSlot();
        mapper.fillEventSlot(request, slot);
        slot.setEndTime(slot.getBeginTime().plusMinutes(request.getUnitDurationMinutes()));
        slot.setProperty(property);

        final SlotUnit unit = new SlotUnit();
        unit.setCapacity(request.getInitialCapacity());
        unit.setInitialCapacity(request.getInitialCapacity());
        unit.setOffset(0);
        slot.addUnit(unit);

        eventSlotDao.persist(slot);

        final NotificationGroup notificationGroup = new NotificationGroup();
        notificationGroupDao.persist(notificationGroup);

        for ( final Account attendant : getEventAttendants(slot) ) {
            // notify attendant by push notification
            pushNotificationSender.sendNotification(new EventNotificationTemplate(slot), attendant);
            // invite attendant to event
            slot.addEventInvite(createEventInvite(slot, attendant, notificationGroup));
        }

        return slot;
    }

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }

    public EventSlot deleteEventSlotById(@Nonnull Long slotId) throws IOException, TemplateException {
        Objects.requireNonNull(slotId);

        final EventSlot slot = getOrElseThrow(slotId, eventSlotDao, new EntityNotFoundException(String.format("Slot id=%d not found", slotId)));

//        for ( final Account attendant : getEventAttendants(slot) ) {
//            // notify attendant by push notification
//            pushNotificationSender.sendNotification(new EventNotificationCancelTemplate(slot), attendant);
//        }

        eventSlotDao.delete(slot);

        return slot;
    }

    public List<EventSlot> getEventSlotsByPropertyIdAndTimeRange(@Nonnull Long propertyId, @Nonnull LocalDate beginDate, @Nonnull LocalDate endDate) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(beginDate);
        Objects.requireNonNull(endDate);

        final Property property = propertyService.getProperty(propertyId);

        final OffsetDateTime beginDt = beginDate.atStartOfDay().atOffset(property.getZoneOffset(beginDate.atStartOfDay()));
        final OffsetDateTime endDt = endDate.atTime(23, 59, 59).atOffset(property.getZoneOffset(endDate.atTime(23, 59, 59)));

        return eventSlotDao.findByPropertyIdAndAccountAndStartBetween(propertyId, authorizationManager.getCurrentAccount(), beginDt, endDt);
    }

    public EventSlotDetailDto getEventDetail(@Nonnull Long slotId) {
        Objects.requireNonNull(slotId);

        final EventSlot eventSlot = getOrElseThrow(slotId, eventSlotDao, new EntityNotFoundException(String.format("Slot id=%d not found", slotId)));

        final Account account = authorizationManager.getCurrentAccount();
        if ( !(authorizationManager.hasAnyOfRoles(AccountRole.PropertyManager, AccountRole.AssistantPropertyManager)) ) {
            if ( !(eventInviteDao.isUserInvitedToEvent(slotId, account)) ) {
                // invite uninvited users to event
                final NotificationGroup notificationGroup = new NotificationGroup();
                notificationGroupDao.persist(notificationGroup);
                createEventInvite(eventSlot, account, notificationGroup);
            }
        }

        final EventSlotDetailDto detailDto = mapper.toEventSlotDetailDto(eventSlot);
        final List<EventInvite> invites = eventInviteDao.findByEventSlotIdOrderByAttendantFirstNameAsc(slotId);
        detailDto.setResponses(invites.stream().map(invite -> {
            final EventSlotDetailDto.Rsvp dao = new EventSlotDetailDto.Rsvp();
            dao.setResponse(invite.getResponse());
            dao.setAttendant(mapper.toEventSlotDetailAccountDto(invite.getAttendant()));
            return dao;
        }).collect(Collectors.toList()));

        return detailDto;
    }

    public void respondToEventInvite(@Nonnull Long slotId, @Nonnull EventInviteResponse response) {
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

    private Slot createMaintenanceSlotFromSchedule(@Nonnull LocalDate date, @Nonnull MaintenanceSlotSchedule schedule) {
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

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public MaintenanceSlotSchedule createSchedule(long propertyId, PersistMaintenanceSlotScheduleRequest request) throws SecurityException {
        final Property property = propertyDao.findById(propertyId);
        if ( authorizationManager.isManager(property) || authorizationManager.isOwner(property) || AccountRole.Administrator.equals(authorizationManager.getCurrentAccount().getRole())) {

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

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
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

    @Scheduled(cron = "0 */10 * * * *") // run every 10th minute (eg. 09:00, 09:10, 09:20, ...)
    public void sendEventRsvpReminder() {

        final List<EventInvite> invitesToNotify = eventInviteDao.findBySlotDateAndInviteResponseAndRemindedAtNull(
                OffsetDateTime.now(), OffsetDateTime.now().plusDays(1), new EventInviteResponse[]{EventInviteResponse.Going, EventInviteResponse.Maybe});


        for ( EventInvite invite : invitesToNotify ) {

            boolean wasReminded = false;

            try {
                pushNotificationSender.sendNotification(new com.creatix.message.template.push.RsvpReminderMessageTemplate(invite), invite.getAttendant());
                wasReminded = true;
            }
            catch ( IOException | TemplateException e ) {
                logger.info(String.format("Failed to push notify %s about upcoming event", invite.getAttendant().getPrimaryEmail()), e);
            }

            try {
                boolean smsEnabled = true;
                if ( invite.getAttendant() instanceof Tenant ) {
                    smsEnabled = ((Tenant) invite.getAttendant()).getEnableSms() == Boolean.TRUE;
                }

                if ( smsEnabled ) {
                    smsMessageSender.send(new com.creatix.message.template.sms.RsvpReminderMessageTemplate(invite));
                    wasReminded = true;
                }
            }
            catch ( Exception e ) {
                logger.info(String.format("Failed to sms notify %s about upcoming event", invite.getAttendant().getPrimaryEmail()), e);
            }

            invite.setRemindedAt(OffsetDateTime.now());
            eventInviteDao.persist(invite);

            if ( !(wasReminded) ) {
                logger.warn(String.format("Failed to notify %s about upcoming event", invite.getAttendant().getPrimaryEmail()));
            }
        }
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

    private void releaseSlot(@Nonnull MaintenanceSlot slot) {
        Objects.requireNonNull(slot, "Slot is null");

        if ( slot.getUnits() != null ) {
            try {
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
            catch ( Exception e ) {
                logger.warn("Failed to release maintenance slot. slot_id={}", slot.getId(), e);
            }
        }
    }

    private @Nonnull EventInvite createEventInvite(@Nonnull EventSlot slot, @Nonnull Account recipient, @Nonnull NotificationGroup group) {
        final EventInvite invite = new EventInvite();
        invite.setAttendant(recipient);
        slot.addEventInvite(invite);
        invite.setResponse(EventInviteResponse.Invited);
        eventInviteDao.persist(invite);

        final EventInviteNotification notification = createEventNotification(invite, authorizationManager.getCurrentAccount(), group);
        invite.setNotification(notification);

        return invite;
    }

    private @Nonnull EventInviteNotification createEventNotification(@Nonnull EventInvite eventInvite, @Nonnull Account eventCreator, @Nonnull NotificationGroup group) {
        final EventInviteNotification notification = new EventInviteNotification();
        group.addNotification(notification);
        notification.setEventInvite(eventInvite);
        notification.setAuthor(eventCreator);
        notification.setTitle("New event invite!");
        notification.setDescription("Hey, there is a new event invite waiting for you!");
        notification.setProperty(eventInvite.getEvent().getProperty());
        notification.setRecipient(eventInvite.getAttendant());
        notification.setStatus(NotificationStatus.Pending);
        notificationDao.persist(notification);
        return notification;
    }

    private List<Account> getEventAttendants(@Nonnull EventSlot slot) {
        final List<Account> attendants;
        if ( slot.getAudience() == AudienceType.Employees ) {
            attendants = accountService.getAccounts(new AccountRole[]{AccountRole.Maintenance, AccountRole.Security, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager}, slot.getProperty().getId());
        }
        else if ( slot.getAudience() == AudienceType.Tenants ) {
            attendants = accountService.getAccounts(new AccountRole[]{AccountRole.Tenant, AccountRole.SubTenant}, slot.getProperty().getId());
        }
        else if ( slot.getAudience() == AudienceType.Everyone ) {
            attendants = accountService.getAccounts(AccountRole.values(), slot.getProperty().getId());
        }
        else {
            attendants = Collections.emptyList();
        }
        return attendants;
    }

    public EventSlot storeEventSlotPhotos(MultipartFile[] files, long eventSlotId) {
        final EventSlot eventSlot = eventSlotDao.findById(eventSlotId);
        List<EventPhoto> photoStoreList;
        try {
            photoStoreList = attachmentService.storeAttachments(files, foreignKeyObject -> {
                EventPhoto eventPhoto = new EventPhoto();
                eventPhoto.setEventSlot(eventSlot);
                return eventPhoto;
            }, eventSlot, EventPhoto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to store photo for event slot", e);
        }

        eventSlot.getEventPhotos().addAll(photoStoreList);
        eventSlotDao.persist(eventSlot);

        return eventSlot;
    }

}
