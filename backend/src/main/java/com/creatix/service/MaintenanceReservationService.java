package com.creatix.service;

import com.creatix.domain.SlotUtils;
import com.creatix.domain.dao.MaintenanceNotificationDao;
import com.creatix.domain.dao.ReservationDao;
import com.creatix.domain.dao.SlotUnitDao;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationResponseRequest;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.MaintenanceSlot;
import com.creatix.domain.entity.store.SlotUnit;
import com.creatix.domain.entity.store.account.MaintenanceEmployee;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.ReservationStatus;
import com.creatix.message.template.push.MaintenanceConfirmTemplate;
import com.creatix.message.template.push.MaintenanceRescheduleConfirmTemplate;
import com.creatix.message.template.push.MaintenanceRescheduleRejectTemplate;
import com.creatix.message.template.push.MaintenanceRescheduleTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.PushNotificationSender;
import freemarker.template.TemplateException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaintenanceReservationService {

    private static final int DEFAULT_RESERVATION_CAPACITY = 1;

    private static final Object syncLock = new Object();

    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private SlotUnitDao slotUnitDao;
    @Autowired
    private PushNotificationSender pushNotificationSender;
    @Autowired
    private MaintenanceNotificationDao maintenanceNotificationDao;

    @RoleSecured
    MaintenanceReservation createMaintenanceReservation(@Nonnull MaintenanceNotification maintenanceNotification, @Nonnull Long slotUnitId) throws IOException, TemplateException {
        Objects.requireNonNull(maintenanceNotification, "Maintenance notification is null");
        Objects.requireNonNull(slotUnitId, "Slot unit id is null");

        final SlotUnit slotUnit = slotUnitDao.findById(slotUnitId);
        if ( slotUnit == null ) {
            throw new EntityNotFoundException(String.format("Slot unit id=%d not found", slotUnitId));
        }

        final MaintenanceSlot slot = (MaintenanceSlot) slotUnit.getSlot();

        final MaintenanceEmployee employee;
        if ( authorizationManager.getCurrentAccount() instanceof MaintenanceEmployee ) {
            employee = (MaintenanceEmployee) authorizationManager.getCurrentAccount();
        }
        else {
            employee = null;
        }

        if ( slotUnit.getBeginTime().isBefore(OffsetDateTime.now()) ) {
            throw new IllegalArgumentException("Cannot create reservation in the past");
        }

        final MaintenanceReservation reservation = new MaintenanceReservation();
        reservation.setSlot(slot);
        reservation.setStatus(ReservationStatus.Pending);
        reservation.setDurationMinutes(slot.getUnitDurationMinutes());
        reservation.setBeginTime(slot.getBeginTime().with(slotUnit.getBeginTime()));
        reservation.setEndTime(reservation.getBeginTime().plusMinutes(slot.getUnitDurationMinutes()));
        reservation.setCapacity(DEFAULT_RESERVATION_CAPACITY);
        reservation.setEmployee(employee);
        reservation.setNote(null);
        reservation.setNotification(maintenanceNotification);
        reserveCapacity(reservation);
        reservationDao.persist(reservation);

        maintenanceNotification.addReservation(reservation);

        return reservation;
    }


    @RoleSecured({AccountRole.Maintenance, AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public MaintenanceReservation deleteById(long reservationId) {
        final MaintenanceReservation reservation = reservationDao.findById(reservationId);
        if ( reservation == null ) {
            throw new EntityNotFoundException(String.format("Reservation id=%d not found", reservationId));
        }
        if ( reservation.getBeginTime().isBefore(OffsetDateTime.now()) ) {
            throw new IllegalArgumentException("Cannot delete reservation in past");
        }

        releaseReservedCapacity(reservation);
        resolveNotification(reservation);

        reservationDao.delete(reservation);
        return reservation;
    }

    @RoleSecured(AccountRole.Maintenance)
    public MaintenanceNotification employeeRespondToMaintenanceNotification(@Nonnull MaintenanceNotification notification, @Nonnull MaintenanceNotificationResponseRequest response) throws IOException, TemplateException {
        Objects.requireNonNull(notification, "Notification is null");
        Objects.requireNonNull(response, "Notification response dto is null");

        notification.setRespondedAt(OffsetDateTime.now());
        maintenanceNotificationDao.persist(notification);

        final List<MaintenanceReservation> reservations = notification.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.Pending).collect(Collectors.toList());
        final long pendingCount = reservations.size();
        if ( pendingCount == 0 ) {
            throw new IllegalArgumentException("No pending reservations found for notification");
        }
        if ( pendingCount > 1 ) {
            Iterator<MaintenanceReservation> reservationIterator = reservations.iterator();
            while (reservationIterator.hasNext()) {
                MaintenanceReservation reservation = reservationIterator.next();
                if (reservation.getUnits().size() > 1) {
                    throw new IllegalArgumentException("Too many slotUnit in reservation.");
                }
                if (reservation.getId() != response.getSlotUnitId()) {
                    releaseReservedCapacity(reservation);
                    notification.getReservations().remove(reservation);
                    reservationIterator.remove();
                    reservationDao.delete(reservation);
                }
            }
        }

        final MaintenanceReservation reservation = reservations.get(0);

        final MaintenanceNotificationResponseRequest.ResponseType responseType = response.getResponse();
        if ( responseType == MaintenanceNotificationResponseRequest.ResponseType.Confirm ) {
            return employeeConfirmReservation(reservation, response.getNote()).getNotification();
        }
        else if ( responseType == MaintenanceNotificationResponseRequest.ResponseType.Reschedule ) {
            return employeeRescheduleReservation(reservation, response.getSlotUnitId(), response.getNote()).getNotification();
        }
        else {
            throw new IllegalArgumentException("Unsupported response type: " + responseType);
        }
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    @Nonnull
    public MaintenanceNotification tenantRespondToMaintenanceReschedule(@Nonnull MaintenanceNotification notification, @Nonnull MaintenanceNotificationResponseRequest response) throws IOException, TemplateException {
        Objects.requireNonNull(notification, "Notification is null");
        Objects.requireNonNull(response, "Notification response dto is null");

        final List<MaintenanceReservation> reservations = notification.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.Pending).collect(Collectors.toList());
        final long pendingCount = reservations.size();
        if ( pendingCount == 0 ) {
            throw new IllegalArgumentException("No pending reservations found for notification");
        }
        if ( pendingCount > 1 ) {
            throw new IllegalStateException("Multiple pending reservations found for notification");
        }

        final MaintenanceReservation reservation = reservations.get(0);

        if ( !(authorizationManager.hasAnyOfRoles(AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager)) && !(Objects.equals(authorizationManager.getCurrentAccount(), notification.getAuthor()))) {
            throw new SecurityException(String.format("You are not allowed to modify maintenance reservation id=%d", reservation.getId()));
        }


        final MaintenanceNotificationResponseRequest.ResponseType responseType = response.getResponse();
        if ( responseType == MaintenanceNotificationResponseRequest.ResponseType.Confirm ) {
            reservation.setStatus(ReservationStatus.Confirmed);
            reservationDao.persist(reservation);
            resolveNotification(reservation);

            pushNotificationSender.sendNotification(new MaintenanceRescheduleConfirmTemplate(reservation, authorizationManager.getCurrentAccount().getFullName()), reservation.getEmployee());
        }
        else if ( responseType == MaintenanceNotificationResponseRequest.ResponseType.Reject ) {
            reservation.setStatus(ReservationStatus.Rejected);
            releaseReservedCapacity(reservation);
            reservationDao.persist(reservation);
            resolveNotification(reservation);

            pushNotificationSender.sendNotification(new MaintenanceRescheduleRejectTemplate(reservation, authorizationManager.getCurrentAccount().getFullName()), reservation.getEmployee());
        }
        else {
            throw new IllegalArgumentException("Unsupported response type: " + responseType);
        }

        return notification;
    }

    @RoleSecured(AccountRole.Maintenance)
    @Nonnull
    MaintenanceReservation employeeConfirmReservation(@Nonnull MaintenanceReservation reservation, String note) throws IOException, TemplateException {
        Objects.requireNonNull(reservation, "Reservation is null");

        if ( reservation.getEmployee() == null ) {
            reservation.setEmployee((MaintenanceEmployee) authorizationManager.getCurrentAccount());
        }
        else {
            if ( !(Objects.equals(authorizationManager.getCurrentAccount(), reservation.getEmployee())) ) {
                throw new SecurityException(String.format("You are not allowed to modify maintenance reservation id=%d", reservation.getId()));
            }
        }

        reservation.setStatus(ReservationStatus.Confirmed);
        reservation.setNote(note);
        reservationDao.persist(reservation);

        resolveNotification(reservation);
        pushNotificationSender.sendNotification(new MaintenanceConfirmTemplate(reservation), reservation.getNotification().getAuthor());

        return reservation;
    }

    @RoleSecured(AccountRole.Maintenance)
    @Nonnull
    MaintenanceReservation employeeRescheduleReservation(@Nonnull MaintenanceReservation reservationOld, @Nonnull Long slotUnitId, String note) throws IOException, TemplateException {
        Objects.requireNonNull(reservationOld, "Reservation old is null");
        Objects.requireNonNull(slotUnitId, "Slot unit ID is null");

        if ( reservationOld.getEmployee() == null ) {
            reservationOld.setEmployee((MaintenanceEmployee) authorizationManager.getCurrentAccount());
        }
        else {
            if ( !(Objects.equals(authorizationManager.getCurrentAccount(), reservationOld.getEmployee())) ) {
                throw new SecurityException(String.format("You are not allowed to modify maintenance reservation id=%d", reservationOld.getId()));
            }
        }

        reservationOld.setStatus(ReservationStatus.Rescheduled);
        reservationOld.setNote(note);
        reservationDao.persist(reservationOld);


        // release capacity held by old reservation
        releaseReservedCapacity(reservationOld);


        final SlotUnit slotUnit = slotUnitDao.findById(slotUnitId);
        if ( slotUnit == null ) {
            throw new EntityNotFoundException(String.format("Slot unit id=%d not found", slotUnitId));
        }

        final MaintenanceNotification notification = reservationOld.getNotification();
        notification.setStatus(NotificationStatus.Pending);
        maintenanceNotificationDao.persist(notification);
        final MaintenanceSlot slot = (MaintenanceSlot) slotUnit.getSlot();

        final MaintenanceReservation reservationNew = new MaintenanceReservation();
        reservationNew.setSlot(slot);
        reservationNew.setStatus(ReservationStatus.Pending);
        reservationNew.setDurationMinutes(reservationOld.getDurationMinutes());
        reservationNew.setBeginTime(slotUnit.getBeginTime());
        reservationNew.setEndTime(slotUnit.getEndTime());
        reservationNew.setCapacity(reservationOld.getCapacity());
        reservationNew.setEmployee(reservationOld.getEmployee());
        reservationNew.setNote(reservationOld.getNote());
        reservationNew.setNotification(notification);

        reserveCapacity(reservationNew);
        reservationDao.persist(reservationNew);

        notification.getReservations().add(reservationNew);

        pushNotificationSender.sendNotification(new MaintenanceRescheduleTemplate(reservationOld, reservationNew), notification.getAuthor());

        return reservationNew;
    }

    private void resolveNotification(@Nonnull MaintenanceReservation maintenanceReservation) {
        Objects.requireNonNull(maintenanceReservation);


        final MaintenanceNotification notification = maintenanceReservation.getNotification();
        Objects.requireNonNull(notification, "Maintenance is missing notification");

        notification.setStatus(NotificationStatus.Resolved);
        maintenanceNotificationDao.persist(notification);
    }

    private void reserveCapacity(MaintenanceReservation reservation) {
        final MaintenanceSlot slot = reservation.getSlot();

        final int unitCount = SlotUtils.calculateUnitCount(reservation.getDurationMinutes(), slot.getUnitDurationMinutes());
        if ( unitCount == 0 ) {
            throw new IllegalArgumentException("Invalid duration of zero minutes");
        }
        synchronized ( syncLock ) {
            // assign slots units to reservation
            final int unitOffsetLft = SlotUtils.calculateUnitOffset(slot, reservation.getBeginTime());
            final int unitOffsetRgt = unitOffsetLft + unitCount - 1;
            slot.getUnits().stream()
                    .filter(u -> ((unitOffsetLft <= u.getOffset()) && (u.getOffset() <= unitOffsetRgt)))
                    .forEach(reservation::addUnit);
            if ( (reservation.getUnits() == null) || (reservation.getUnits().size() != unitCount) ) {
                throw new IllegalArgumentException("Reservation does not fit into slot.");
            }
            // update slot unit capacity
            reservation.getUnits().forEach(unit -> {
                unit.setCapacity(unit.getCapacity() - reservation.getCapacity());
                if ( unit.getCapacity() < 0 ) {
                    throw new IllegalArgumentException("Insufficient capacity");
                }
                slotUnitDao.persist(unit);
            });
        }
    }

    private void releaseReservedCapacity(MaintenanceReservation reservation) {
        synchronized ( syncLock ) {
            // free slot unit capacity
            reservation.getUnits().forEach(unit -> {
                unit.setCapacity(unit.getCapacity() + reservation.getCapacity());
                slotUnitDao.persist(unit);
            });
        }
    }


}
