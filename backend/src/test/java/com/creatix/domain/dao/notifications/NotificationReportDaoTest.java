package com.creatix.domain.dao.notifications;

import com.creatix.domain.dao.NotificationHistoryDao;
import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportAccountDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGlobalInfoDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGroupByAccountDto;
import com.creatix.domain.entity.store.*;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.domain.entity.store.notification.NotificationGroup;
import com.creatix.domain.entity.store.notification.NotificationHistory;
import com.creatix.domain.enums.*;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.api.Randomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test for DAO layer.
 * <p>
 * Developer or CI has to be sure that database is running on predefined location
 * <p>
 * for example
 * on your local machine without using environment variables - localhost:5432/aptapp with user aptapp password aptapp
 * or using environment variables see <a href="file:../resources/application.properties>application.properties</a>
 */
@RunWith(SpringRunner.class)
@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationReportDaoTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @SpyBean
    private NotificationReportDao notificationReportDao;

    @SpyBean
    private NotificationHistoryDao notificationHistoryDao;

    private EnhancedRandom random;

    private static final OffsetDateTime NOW = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    private NotificationGroup notificationGroup;
    private Property property;
    private PropertyOwner author;
    private MaintenanceEmployee technician1;
    private MaintenanceEmployee technician2;

    private List<NotificationHistory> histories = new LinkedList<>();
    private Apartment apartment1;
    private Apartment apartment2;
    private Tenant author2;

    @Before
    public void setup() {
        assertNotNull(testEntityManager);
        assertNotNull(notificationReportDao);

        random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .randomizationDepth(1)
                .scanClasspathForConcreteTypes(true)
                .overrideDefaultInitialization(false)
                .randomize(FieldDefinitionBuilder.field().named("title").ofType(String.class).inClass(Notification.class).get(), new StringRandomizer(20))
                .randomize(FieldDefinitionBuilder.field().named("primaryEmail").ofType(String.class).inClass(Account.class).get(), (Randomizer<String>) () -> "test@test.com")
                .build();

        // clean up data from database
        testEntityManager.getEntityManager().createNativeQuery("DELETE FROM notification_history").executeUpdate();

        notificationGroup = new NotificationGroup();
        notificationGroup.setCreatedAt(NOW);
        testEntityManager.persistAndFlush(notificationGroup);

        author = random.nextObject(PropertyOwner.class, "id", "secondaryEmail");
        testEntityManager.persistAndFlush(author);

        technician1 = random.nextObject(MaintenanceEmployee.class, "id", "secondaryEmail", "manager", "role");
        technician1.setRole(AccountRole.Maintenance);
        testEntityManager.persistAndFlush(technician1);

        technician2 = random.nextObject(MaintenanceEmployee.class, "id", "secondaryEmail", "manager", "role");
        technician2.setRole(AccountRole.Maintenance);
        testEntityManager.persistAndFlush(technician2);

        Address address = random.nextObject(Address.class, "id");
        testEntityManager.persistAndFlush(address);

        property = random.nextObject(Property.class, "id", "owner", "address", "schedule", "contacts");
        property.setOwner(author);
        property.setAddress(address);
        testEntityManager.persistAndFlush(property);

        apartment1 = random.nextObject(Apartment.class, "id", "tenant", "floor");
        apartment1.setProperty(property);
        testEntityManager.persistAndFlush(apartment1);

        apartment2 = random.nextObject(Apartment.class, "id", "tenant", "floor");
        apartment2.setProperty(property);
        testEntityManager.persistAndFlush(apartment2);

        author2 = random.nextObject(Tenant.class, "id", "secondaryEmail");
        author2.setApartment(apartment1);
        testEntityManager.persistAndFlush(author2);
    }

    @Test
    public void getGlobalInfo() {
        // have one notification resolved and pass due date confirmed after begin time of reservation
        NotificationHistory notificationHistory = createMaintenanceNotification(NOW, technician1);

        notificationHistory = changeNotificationStatus(notificationHistory, NOW.plusDays(1L), null, NotificationStatus.Resolved, NotificationHistoryStatus.Confirmed, ReservationStatus.Pending);
        changeNotificationStatus(notificationHistory, NOW.plusDays(2L), NOW.plusDays(2L), NotificationStatus.Resolved, NotificationHistoryStatus.Resolved, ReservationStatus.Confirmed);

        // have one notification confirmed but not resolved (not pass due date)
        notificationHistory = createMaintenanceNotification(technician1);

        changeNotificationStatus(notificationHistory, NOW.plusDays(1L), null, NotificationStatus.Resolved, NotificationHistoryStatus.Confirmed, ReservationStatus.Pending);

        // two pass due date not confirmed pending notifications (also reservations)
        createMaintenanceNotification(NOW.minusDays(2), technician1);
        createMaintenanceNotification(NOW.minusDays(3), technician1);

        // one not pass due date open notification
        createMaintenanceNotification(technician1);

        NotificationReportGlobalInfoDto report = notificationReportDao.getMaintenanceGlobalInfo(NOW.minusDays(1L), NOW.plusDays(1L), property.getId());/**/

        Long requests = 5L;
        assertEquals(requests, report.getRequests());

        Double openRequests = 60D;
        assertEquals(openRequests, report.getOpenRequests());

        Long passDueDate = 3L;
        assertEquals(passDueDate, report.getPastDueDateRequests());

        // average times are computed but not tests because currently there is used @PrePersist.
        //  which prevent us to mock createdAt for NotificationHistory entity
        //  this test should be covered by E2E or executing pure SQL statements.
    }

    @Test
    public void getNotificationReportMaintenance() {
        createMaintenanceNotification(NOW, technician1, apartment1);
        NotificationHistory notificationHistory;

        notificationHistory = createMaintenanceNotification(NOW, technician2, apartment2);
        changeNotificationStatus(notificationHistory, NOW.plusDays(1L), null, NotificationStatus.Resolved, NotificationHistoryStatus.Confirmed, ReservationStatus.Pending, technician1);

        notificationHistory = createMaintenanceNotification(NOW, author2, technician2, apartment2);
        notificationHistory = changeNotificationStatus(notificationHistory, NOW.plusDays(1L), null, NotificationStatus.Resolved, NotificationHistoryStatus.Confirmed, ReservationStatus.Pending, technician2);
        changeNotificationStatus(notificationHistory, NOW.plusDays(2L), NOW.plusDays(2L), NotificationStatus.Resolved, NotificationHistoryStatus.Resolved, ReservationStatus.Confirmed, technician1);

        List<NotificationReportDto> reports = notificationReportDao.getNotificationReport(NOW.minusDays(1L), NOW.plusDays(1L), NotificationType.Maintenance, property.getId());

        assertEquals(3, reports.size());

        // check first report which is still pending
        NotificationReportDto report = reports.get(0);

        assertEquals(NotificationHistoryStatus.Pending.name(), report.getStatus());

        NotificationReportAccountDto account;
        account = report.getCreatedBy();

        assertEquals(author.getId(), account.getId());

        BasicApartmentDto apartment = account.getApartment();
        //assertEquals(aparment.getId(), apartment1.getId());
        assertNull(apartment);

        apartment = report.getTargetApartment();
        assertEquals(apartment1.getId(), apartment.getId());

        assertNull(report.getRespondedBy());
        assertNull(report.getResolvedBy());

        // check report which is confirmed
        report = reports.get(1);

        assertEquals(NotificationHistoryStatus.Confirmed.name(), report.getStatus());

        account = report.getCreatedBy();

        assertEquals(author.getId(), account.getId());

        apartment = account.getApartment();
        //assertEquals(apartment.getId(), apartment1.getId());
        assertNull(apartment);

        apartment = report.getTargetApartment();
        assertEquals(apartment2.getId(), apartment.getId());

        account = report.getRespondedBy();
        assertEquals(technician1.getId(), account.getId());

        assertNull(report.getResolvedBy());

        report = reports.get(2);

        assertEquals(NotificationHistoryStatus.Resolved.name(), report.getStatus());

        account = report.getCreatedBy();

        assertEquals(author2.getId(), account.getId());

        apartment = account.getApartment();
        assertEquals(apartment.getId(), apartment1.getId());

        apartment = report.getTargetApartment();
        assertEquals(apartment2.getId(), apartment.getId());

        account = report.getRespondedBy();
        assertEquals(technician2.getId(), account.getId());

        account = report.getResolvedBy();
        assertEquals(technician1.getId(), account.getId());
    }

    @Test
    public void getNotificationReportGroupedByAccount() {
        NotificationHistory notificationHistory;

        // create notifications for technician1
        createMaintenanceNotification(NOW, technician1);
        for (int i = 0; i < 2; i++) {
            notificationHistory = createMaintenanceNotification(NOW, author, technician1);
            changeNotificationStatus(notificationHistory, NOW.plusDays(1L), null, NotificationStatus.Resolved, NotificationHistoryStatus.Confirmed, ReservationStatus.Pending, technician1);
        }

        for (int i = 0; i < 3; i++) {
            notificationHistory = createMaintenanceNotification(NOW, technician1);

            notificationHistory = changeNotificationStatus(notificationHistory, NOW.plusDays(1L), null, NotificationStatus.Resolved, NotificationHistoryStatus.Confirmed, ReservationStatus.Pending, technician1);
            changeNotificationStatus(notificationHistory, NOW.plusDays(2L), NOW.plusDays(2L), NotificationStatus.Resolved, NotificationHistoryStatus.Resolved, ReservationStatus.Confirmed, technician1);
        }

        // craete notifications for technician2
        createMaintenanceNotification(NOW, technician2);
        createMaintenanceNotification(NOW, technician2);

        notificationHistory = createMaintenanceNotification(NOW, technician2);

        notificationHistory = changeNotificationStatus(notificationHistory, NOW.plusDays(1L), null, NotificationStatus.Resolved, NotificationHistoryStatus.Confirmed, ReservationStatus.Pending, technician2);
        changeNotificationStatus(notificationHistory, NOW.plusDays(2L), NOW.plusDays(2L), NotificationStatus.Resolved, NotificationHistoryStatus.Resolved, ReservationStatus.Confirmed, technician2);

        List<NotificationReportGroupByAccountDto> reports = notificationReportDao.getNotificationReportGroupedByAccount(NOW.minusDays(1L), NOW.plusDays(1L), NotificationType.Maintenance, AccountRole.Maintenance, property.getId());

        assertEquals(2, reports.size());
        NotificationReportGroupByAccountDto report = reports.get(0);

        Long confirmed = 5L;
        Long resolved = 3L;
        assertEquals(confirmed, report.getConfirmed());
        assertEquals(resolved, report.getResolved());
        assertEquals(technician1.getId(), report.getAccount().getId());

        report = reports.get(1);

        confirmed = 1L;
        resolved = 1L;
        assertEquals(confirmed, report.getConfirmed());
        assertEquals(resolved, report.getResolved());
        assertEquals(technician2.getId(), report.getAccount().getId());

        Long average = 0L;
        assertNotEquals(average, report.getAverageTimeToConfirm());
    }

    private NotificationHistory changeNotificationStatus(NotificationHistory notificationHistory, OffsetDateTime when, OffsetDateTime responded,
                                                         NotificationStatus notificationStatus, NotificationHistoryStatus notificationHistoryStatus, ReservationStatus reservationStatus) {
        return changeNotificationStatus(notificationHistory, when, responded, notificationStatus, notificationHistoryStatus, reservationStatus, author);
    }

    private NotificationHistory changeNotificationStatus(NotificationHistory notificationHistory, OffsetDateTime when, OffsetDateTime responded,
                                                         NotificationStatus notificationStatus, NotificationHistoryStatus notificationHistoryStatus, ReservationStatus reservationStatus, Account author) {
        MaintenanceNotification notification = (MaintenanceNotification) notificationHistory.getNotification();
        notification.setStatus(notificationStatus);
        notification.setUpdatedAt(when);
        notification.setRespondedAt(responded);

        testEntityManager.persistAndFlush(notification);

        MaintenanceReservation maintenanceReservation = notification.getReservations().get(0);
        maintenanceReservation.setStatus(reservationStatus);

        notificationHistory = createNotificationHistory(notification, author, notificationHistoryStatus, when);
        testEntityManager.persistAndFlush(notificationHistory);
        histories.add(notificationHistory);

        return notificationHistory;
    }

    private NotificationHistory createMaintenanceNotification(ManagedEmployee employee) {
        return createMaintenanceNotification(NOW.plusDays(1), author, employee);
    }

    private NotificationHistory createMaintenanceNotification(OffsetDateTime reservationBegin, ManagedEmployee employee) {
        return createMaintenanceNotification(reservationBegin, author, employee);
    }

    private NotificationHistory createMaintenanceNotification(OffsetDateTime reservationBegin, ManagedEmployee employee, Apartment apartment) {
        return createMaintenanceNotification(reservationBegin, author, employee, apartment);
    }

    private NotificationHistory createMaintenanceNotification(OffsetDateTime reservationBegin, Account author, ManagedEmployee employee) {
        return createMaintenanceNotification(reservationBegin, author, employee, apartment1);
    }

    private NotificationHistory createMaintenanceNotification(OffsetDateTime reservationBegin, Account author, ManagedEmployee employee, Apartment apartment) {
        MaintenanceNotification maintenanceNotification = generateMaintenanceNotification(NotificationStatus.Pending, author);
        maintenanceNotification.setTargetApartment(apartment);
        testEntityManager.persistAndFlush(maintenanceNotification);

        NotificationHistory notificationHistory = createNotificationHistory(maintenanceNotification, author, NotificationHistoryStatus.Pending, NOW);
        testEntityManager.persistAndFlush(notificationHistory);
        histories.add(notificationHistory);

        createMaintenanceReservation(maintenanceNotification, reservationBegin, ReservationStatus.Pending, employee);

        return notificationHistory;
    }

    private NotificationHistory createNotificationHistory(Notification notification, Account author, NotificationHistoryStatus status, OffsetDateTime createdAt) {
        NotificationHistory notificationHistory = new NotificationHistory();
        notificationHistory.setType(notification.getType());
        notificationHistory.setProperty(notification.getProperty());
        notificationHistory.setAuthor(author);
        notificationHistory.setNotification(notification);
        notificationHistory.setStatus(status);
        notificationHistory.setCreatedAt(createdAt);

        return notificationHistory;
    }

    private void createMaintenanceReservation(MaintenanceNotification notification, OffsetDateTime beginTime, ReservationStatus status, ManagedEmployee employee) {
        MaintenanceReservation maintenanceReservation = random.nextObject(MaintenanceReservation.class, "id", "beginTime", "endTime", "slot", "employee", "units");
        maintenanceReservation.setNotification(notification);
        maintenanceReservation.setBeginTime(beginTime);
        maintenanceReservation.setEndTime(beginTime.plusDays(5L));
        maintenanceReservation.setStatus(status);
        maintenanceReservation.setEmployee(employee);

        MaintenanceSlot slot = random.nextObject(MaintenanceSlot.class, "id", "beginTime", "endTime", "property", "schedule", "units", "reservations", "slots");
        slot.setBeginTime(beginTime);
        slot.setEndTime(beginTime.plusDays(5L));
        slot.setProperty(property);

        testEntityManager.persistAndFlush(slot);

        maintenanceReservation.setSlot(slot);

        notification.addReservation(maintenanceReservation);

        testEntityManager.persistAndFlush(maintenanceReservation);
    }

    private MaintenanceNotification generateMaintenanceNotification(NotificationStatus notificationStatus, Account author) {
        return generateMaintenanceNotification(notificationStatus, NOW, null, author);
    }

    private MaintenanceNotification generateMaintenanceNotification(NotificationStatus notificationStatus, OffsetDateTime updated, OffsetDateTime respondedAt, Account author) {
        MaintenanceNotification maintenanceNotification = random.nextObject(
                MaintenanceNotification.class, "id", "targetApartment", "type", "author",
                "recipient", "property", "notificationGroup", "createdAt", "updatedAt", "deletedAt", "status", "history", "reservations");
        maintenanceNotification.setType(NotificationType.Maintenance);
        maintenanceNotification.setCreatedAt(NOW);
        maintenanceNotification.setUpdatedAt(updated);
        maintenanceNotification.setRespondedAt(updated);
        maintenanceNotification.setStatus(notificationStatus);
        maintenanceNotification.setAuthor(author);
        maintenanceNotification.setNotificationGroup(notificationGroup);
        maintenanceNotification.setProperty(property);

        return maintenanceNotification;
    }
}