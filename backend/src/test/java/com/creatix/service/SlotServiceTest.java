package com.creatix.service;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.dao.SlotDao;
import com.creatix.domain.dto.property.slot.*;
import com.creatix.domain.entity.store.EventInvite;
import com.creatix.domain.entity.store.EventSlot;
import com.creatix.domain.entity.store.MaintenanceSlotSchedule;
import com.creatix.domain.entity.store.Slot;
import com.creatix.domain.enums.AudienceType;
import com.creatix.mock.WithMockCustomUser;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.*;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class SlotServiceTest {

    @Autowired
    private SlotService slotService;
    @Autowired
    private SlotDao slotDao;
    @Autowired
    private NotificationService notificationService;
    @PersistenceContext
    protected EntityManager em;

    @Test
    @WithMockCustomUser("apt@test.com")
    public void getSlotsByFilter() throws Exception {
        ScheduledSlotsResponse result;
        final Long propertyId = 1L;
        LocalDate beginDate = LocalDate.of(2016, 7, 1);
        LocalDate endDate = LocalDate.of(2016, 8, 31);
        result = slotService.getSlotsByFilter(propertyId, beginDate, endDate, null, null);
        assertNotNull(result);
        assertEquals(2, result.getSlots().size());

        Long startId = 100L;
        int pageSize = 4;
        result = slotService.getSlotsByFilter(propertyId, null, null, startId, pageSize);
        assertNotNull(result);
        assertEquals(pageSize, result.getSlots().size());
        assertEquals(startId, result.getSlots().get(0).getId());
        assertEquals(104L, (long) result.getNextId());

        startId = 421L;
        result = slotService.getSlotsByFilter(propertyId, null, null, startId, pageSize);
        assertNotNull(result);
        assertEquals(1, result.getSlots().size());
        assertEquals(startId, result.getSlots().get(0).getId());
        assertNull(result.getNextId());

        result.getSlots().forEach(s -> assertTrue(s.getType() == SlotDto.SlotType.Event));
    }

    @Test
    @WithMockCustomUser("mark.building@apartments.com")
    public void createEventSlot() throws Exception {
        final OffsetDateTime beginTime = OffsetDateTime.of(2016, 4, 1, 14, 30, 0, 0, ZoneOffset.UTC);
        final PersistEventSlotRequest request = new PersistEventSlotRequest();
        request.setBeginTime(beginTime);
        request.setDescription("This is description");
        request.setUnitDurationMinutes(30);
        request.setInitialCapacity(1);
        request.setLocation("Lobby");
        request.setTitle("Meeting");
        request.setAudience(AudienceType.Tenants);

        final EventSlot slot = slotService.createEventSlot(1L, request);
        assertNotNull(slot);
        assertNotNull(slot.getId());
        assertEquals("Lobby", slot.getLocation());
        assertEquals("Meeting", slot.getTitle());
        assertEquals(beginTime, slot.getBeginTime());
        assertEquals(beginTime.plusMinutes(30), slot.getEndTime());
        assertEquals(1, slot.getUnits().size());
        assertEquals(AudienceType.Tenants, slot.getAudience());
        assertEquals(request.getUnitDurationMinutes().intValue(), slot.getUnitDurationMinutes());

        for ( EventInvite invite : slot.getInvites() ) {
            assertNotNull(invite.getNotification());
            assertNotNull(invite.getNotification().getId());
        }
    }

    @Test
    @WithMockCustomUser("mark.building@apartments.com")
    public void scheduleSlots() throws Exception {

        final PersistMaintenanceSlotScheduleRequest request = new PersistMaintenanceSlotScheduleRequest();
        request.setBeginTime(LocalTime.of(10, 0, 0));
        request.setEndTime(LocalTime.of(18, 0, 0));
        request.setDaysOfWeek(EnumSet.allOf(DayOfWeek.class));
        request.setInitialCapacity(1);
        request.setUnitDurationMinutes(60);
        Map<DayOfWeek, DayDuration> durationPerDayOfWeek = ImmutableMap.<DayOfWeek, DayDuration>builder().put(
                DayOfWeek.FRIDAY,
                new DayDuration()
                        .setBeginTime(LocalTime.parse("09:00:00.000"))
                        .setEndTime(LocalTime.parse("17:00:00.000"))
        ).build();
        request.setDurationPerDayOfWeek(durationPerDayOfWeek);
        final MaintenanceSlotSchedule schedule = slotService.createSchedule(1L, request);
        assertNotNull(schedule);
        assertEquals((Long) 1L, schedule.getProperty().getId());
        assertTrue(schedule.getDaysOfWeek().containsAll(request.getDaysOfWeek()));

        slotService.scheduleSlots();
        final List<Slot> slots1 = slotDao.findAll();

        for ( int i = 0; i < 5; ++i ) {
            slotService.scheduleSlots();
            final List<Slot> slots2 = slotDao.findAll();

            assertEquals(slots1.size(), slots2.size());
        }
    }

    @Test
    @WithMockCustomUser("mark.building@apartments.com")
    public void rescheduleSlots() throws Exception {

        final PersistMaintenanceSlotScheduleRequest request = new PersistMaintenanceSlotScheduleRequest();
        request.setBeginTime(LocalTime.of(10, 0, 0));
        request.setEndTime(LocalTime.of(18, 0, 0));
        request.setDaysOfWeek(EnumSet.allOf(DayOfWeek.class));
        request.setInitialCapacity(1);
        request.setUnitDurationMinutes(60);
        Map<DayOfWeek, DayDuration> durationPerDayOfWeek = ImmutableMap.<DayOfWeek, DayDuration>builder().put(
                DayOfWeek.FRIDAY,
                new DayDuration()
                        .setBeginTime(LocalTime.parse("09:00:00.000"))
                        .setEndTime(LocalTime.parse("17:00:00.000"))
        ).build();
        request.setDurationPerDayOfWeek(durationPerDayOfWeek);

        final MaintenanceSlotSchedule schedule1 = slotService.createSchedule(1L, request);
        assertNotNull(schedule1);
        assertEquals((Long) 1L, schedule1.getProperty().getId());
        assertEquals(request.getBeginTime(), schedule1.getBeginTime());
        assertEquals(request.getEndTime(), schedule1.getEndTime());
        assertTrue(schedule1.getDaysOfWeek().containsAll(request.getDaysOfWeek()));
        assertFalse(schedule1.getSlots().isEmpty());

        final Set<MaintenanceSlot> slots1 = schedule1.getSlots();
        slots1.forEach(slot -> assertEquals(request.getBeginTime(), slot.getBeginTime().toLocalTime()));




        request.setBeginTime(LocalTime.of(9, 0, 0));
        final MaintenanceSlotSchedule schedule2 = slotService.createSchedule(1L, request);
        assertEquals(schedule1.getId(), schedule2.getId());
        assertNotNull(schedule2);
        assertEquals((Long) 1L, schedule2.getProperty().getId());
        assertEquals(request.getBeginTime(), schedule2.getBeginTime());
        assertEquals(request.getEndTime(), schedule2.getEndTime());
        assertTrue(schedule2.getDaysOfWeek().containsAll(request.getDaysOfWeek()));
        assertFalse(schedule2.getSlots().isEmpty());

        final Set<MaintenanceSlot> slots2 = schedule1.getSlots();
        slots2.forEach(slot -> assertEquals(request.getBeginTime(), slot.getBeginTime().toLocalTime()));

        final Optional<MaintenanceSlot> optSlot = slots2.stream().findFirst();
        assertTrue(optSlot.isPresent());
        final MaintenanceSlot slot = optSlot.get();
        final Optional<SlotUnit> optUnit = slot.getUnits().stream().findFirst();
        assertTrue(optUnit.isPresent());
        final SlotUnit unit = optUnit.get();

        MaintenanceNotification notification = new MaintenanceNotification();
        notification.setAccessIfNotAtHome(true);
        notification.setTitle("title");
        notification.setDescription("description");
        notification = notificationService.saveMaintenanceNotification("27", notification, unit.getId());
        assertNotNull(notification.getId());
        assertEquals("mark.building@apartments.com", notification.getAuthor().getPrimaryEmail());
        assertEquals(1, notification.getReservations().size());

        em.flush();
        em.clear();

        notification = notificationService.getMaintenanceNotification(notification.getId());
        assertNotNull(notification.getReservations().get(0).getSlot().getSchedule());

        em.flush();
        em.clear();

        request.setBeginTime(LocalTime.of(8, 0, 0));
        final MaintenanceSlotSchedule schedule3 = slotService.createSchedule(1L, request);
        assertEquals(schedule1.getId(), schedule3.getId());

        em.flush();
        em.clear();

        notification = notificationService.getMaintenanceNotification(notification.getId());
        assertNotNull(notification.getId());
        assertEquals("mark.building@apartments.com", notification.getAuthor().getPrimaryEmail());
        assertEquals(1, notification.getReservations().size());
        assertNull(notification.getReservations().get(0).getSlot().getSchedule());
    }

}