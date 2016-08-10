package com.creatix.service;

import com.creatix.domain.dao.*;
import com.creatix.domain.dto.property.RespondToRescheduleRequest;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.MaintenanceSlot;
import com.creatix.domain.entity.store.SlotUnit;
import com.creatix.domain.entity.store.account.ManagedEmployee;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.ReservationStatus;
import com.creatix.message.PushNotificationSender;
import com.creatix.message.template.push.MaintenanceConfirmTemplate;
import com.creatix.message.template.push.MaintenanceRescheduleConfirmTemplate;
import com.creatix.message.template.push.MaintenanceRescheduleRejectTemplate;
import com.creatix.message.template.push.MaintenanceRescheduleTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Objects;

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
    private SlotService slotService;
    @Autowired
    private SlotUnitDao slotUnitDao;
    @Autowired
    private PushNotificationSender pushNotificationSender;
    @Autowired
    private MaintenanceNotificationDao maintenanceNotificationDao;

    @RoleSecured(AccountRole.Maintenance)
    public MaintenanceReservation createMaintenanceReservation(@NotNull MaintenanceNotification maintenanceNotification, @NotNull Long slotUnitId) throws IOException, TemplateException {
        Objects.requireNonNull(maintenanceNotification, "Maintenance notification is null");
        Objects.requireNonNull(slotUnitId, "Slot unit id is null");

        final SlotUnit slotUnit = slotUnitDao.findById(slotUnitId);
        if ( slotUnit == null ) {
            throw new EntityNotFoundException(String.format("Slot unit id=%d not found", slotUnitId));
        }

        final MaintenanceSlot slot = (MaintenanceSlot) slotUnit.getSlot();

        final ManagedEmployee employee = (ManagedEmployee) authorizationManager.getCurrentAccount();
        if ( employee == null ) {
            throw new IllegalStateException("Account is not employee assigned");
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

        return reservation;
    }


    @RoleSecured(AccountRole.Maintenance)
    public MaintenanceReservation deleteById(long reservationId) {
        final MaintenanceReservation reservation = reservationDao.findById(reservationId);
        if ( reservation == null ) {
            throw new EntityNotFoundException(String.format("Reservation id=%d not found", reservationId));
        }
        if ( reservation.getBeginTime().isBefore(OffsetDateTime.now()) ) {
            throw new IllegalArgumentException("Cannot delete reservation in past");
        }

        releaseReservedCapacity(reservation);

        reservationDao.delete(reservation);
        return reservation;
    }

    @RoleSecured(AccountRole.Tenant)
    public MaintenanceReservation tenantRespondToReschedule(@NotNull Long reservationId, @NotNull RespondToRescheduleRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(reservationId, "Reservation id is null");
        Objects.requireNonNull(request, "Request si null");

        final MaintenanceReservation reservation = reservationDao.findById(reservationId);
        if ( reservation == null ) {
            throw new EntityNotFoundException(String.format("Maintenance reservation id=%d not found", reservationId));
        }
        if ( reservation.getNotification() == null ) {
            throw new IllegalArgumentException(String.format("Maintenance reservation id=%d has no notification assigned", reservationId));
        }
        if ( reservation.getStatus() != ReservationStatus.Rescheduled ) {
            throw new IllegalArgumentException(String.format("Maintenance reservation id=%d is not in Rescheduled state", reservationId));
        }
        if ( !(Objects.equals(authorizationManager.getCurrentAccount(), reservation.getNotification().getTargetApartment().getTenant())) ) {
            throw new SecurityException(String.format("You are not allowed to modify maintenance reservation id=%d", reservationId));
        }

        if ( request.getResponseType() == RespondToRescheduleRequest.RescheduleResponseType.Confirm ) {
            reservation.setStatus(ReservationStatus.Confirmed);
            pushNotificationSender.sendNotification(new MaintenanceRescheduleConfirmTemplate(reservation.getNotification()), reservation.getEmployee());
        }
        else if ( request.getResponseType() == RespondToRescheduleRequest.RescheduleResponseType.Reject ) {
            reservation.setStatus(ReservationStatus.Rejected);
            releaseReservedCapacity(reservation);
            pushNotificationSender.sendNotification(new MaintenanceRescheduleRejectTemplate(reservation.getNotification()), reservation.getEmployee());
        }
        else {
            throw new IllegalArgumentException("Unsupported response type: " + request.getResponseType());
        }
        reservationDao.persist(reservation);

        final MaintenanceNotification notification = reservation.getNotification();
        notification.setStatus(NotificationStatus.Resolved);
        maintenanceNotificationDao.persist(notification);

        return reservation;
    }

    public MaintenanceReservation employeeConfirmReservation(@NotNull MaintenanceReservation reservation, String note) throws IOException, TemplateException {
        Objects.requireNonNull(reservation, "Reservation is null");

        if ( !(Objects.equals(authorizationManager.getCurrentAccount(), reservation.getEmployee())) ) {
            throw new SecurityException(String.format("You are not allowed to modify maintenance reservation id=%d", reservation.getId()));
        }

        reservation.setStatus(ReservationStatus.Confirmed);
        reservation.setNote(note);
        reservationDao.persist(reservation);

        final MaintenanceNotification notification = reservation.getNotification();
        notification.setStatus(NotificationStatus.Resolved);
        maintenanceNotificationDao.persist(notification);
        pushNotificationSender.sendNotification(new MaintenanceConfirmTemplate(notification), notification.getAuthor());

        return reservation;
    }

    @NotNull
    public MaintenanceReservation employeeRescheduleReservation(@NotNull MaintenanceReservation reservationOld, @NotNull Long slotUnitId, String note) throws IOException, TemplateException {
        Objects.requireNonNull(reservationOld, "Reservation old is null");
        Objects.requireNonNull(slotUnitId, "Slot unit ID is null");

        if ( !(Objects.equals(authorizationManager.getCurrentAccount(), reservationOld.getEmployee())) ) {
            throw new SecurityException(String.format("You are not allowed to modify maintenance reservation id=%d", reservationOld.getId()));
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
        reservationNew.setStatus(ReservationStatus.Confirmed);
        reservationNew.setDurationMinutes(reservationOld.getDurationMinutes());
        reservationNew.setBeginTime(slot.getBeginTime().with(reservationOld.getBeginTime()));
        reservationNew.setEndTime(reservationNew.getBeginTime().plusMinutes(slot.getUnitDurationMinutes()));
        reservationNew.setCapacity(reservationOld.getCapacity());
        reservationNew.setEmployee(reservationOld.getEmployee());
        reservationNew.setNote(reservationOld.getNote());
        reservationNew.setNotification(notification);

        reserveCapacity(reservationNew);
        reservationDao.persist(reservationNew);

        notification.getReservations().add(reservationNew);

        pushNotificationSender.sendNotification(new MaintenanceRescheduleTemplate(notification), notification.getAuthor());

        return reservationNew;
    }

    private void reserveCapacity(MaintenanceReservation reservation) {
        final MaintenanceSlot slot = reservation.getSlot();

        final int unitCount = slotService.calculateUnitCount(reservation.getDurationMinutes(), slot.getUnitDurationMinutes());
        if ( unitCount == 0 ) {
            throw new IllegalArgumentException("Invalid duration of zero minutes");
        }
        synchronized ( syncLock ) {
            // assign slots units to reservation
            final int unitOffsetLft = slotService.calculateUnitOffset(slot, reservation.getBeginTime());
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
