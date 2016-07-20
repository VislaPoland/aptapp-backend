package com.creatix.service;

import com.creatix.domain.dao.*;
import com.creatix.domain.dto.property.RespondToRescheduleRequest;
import com.creatix.domain.dto.property.slot.PersistMaintenanceReservationRequest;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.MaintenanceSlot;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.SlotUnit;
import com.creatix.domain.entity.store.account.ManagedEmployee;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ReservationStatus;
import com.creatix.message.PushNotificationSender;
import com.creatix.message.template.push.MaintenanceNotificationRescheduleTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MaintenanceReservationService {

    private static final Object syncLock = new Object();

    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private MaintenanceSlotDao maintenanceSlotDao;
    @Autowired
    private SlotService slotService;
    @Autowired
    private SlotUnitDao slotUnitDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private ManagedEmployeeDao managedEmployeeDao;
    @Autowired
    private MaintenanceNotificationDao maintenanceNotificationDao;
    @Autowired
    private PushNotificationSender pushNotificationSender;

    @RoleSecured(AccountRole.Maintenance)
    public MaintenanceReservation createMaintenanceReservation(Long propertyId, PersistMaintenanceReservationRequest reservationRequest) throws IOException, TemplateException {

        final MaintenanceSlot slot = maintenanceSlotDao.findById(reservationRequest.getSlotId());
        if ( slot == null ) {
            throw new EntityNotFoundException(String.format("Slot id=%d not found", reservationRequest.getSlotId()));
        }

        final ManagedEmployee employee = (ManagedEmployee) authorizationManager.getCurrentAccount();
        if ( employee == null ) {
            throw new IllegalStateException("Account is not employee assigned");
        }
        if ( reservationRequest.getBeginTime().isBefore(OffsetDateTime.now()) ) {
            throw new IllegalArgumentException("Cannot create reservation in the past");
        }

        if ( (reservationRequest.getStatus() == ReservationStatus.Rescheduled) && (reservationRequest.getRescheduleTime() == null) ) {
            throw new IllegalArgumentException("Rescheduled status is set but no reschedule time is specified");
        }

        final MaintenanceReservation reservation = new MaintenanceReservation();
        reservation.setStatus(reservationRequest.getStatus());
        if ( reservationRequest.getStatus() == ReservationStatus.Rescheduled ) {
            reservation.setRescheduleTime(reservationRequest.getRescheduleTime());
        }
        reservation.setDurationMinutes(reservationRequest.getDurationMinutes());
        reservation.setBeginTime(slot.getBeginTime().with(reservationRequest.getBeginTime()));
        reservation.setEndTime(reservation.getBeginTime().plusMinutes(slot.getUnitDurationMinutes()));
        reservation.setCapacity(reservationRequest.getCapacity());
        reservation.setEmployee(employee);
        reservation.setNote(reservationRequest.getNote());
        if ( reservationRequest.getNotificationId() != null ) {
            reservation.setNotification(maintenanceNotificationDao.findById(reservationRequest.getNotificationId()));
        }


        final int unitCount = slotService.calculateUnitCount(reservationRequest.getDurationMinutes(), slot.getUnitDurationMinutes());
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
        reservationDao.persist(reservation);

        if ( reservation.getStatus() == ReservationStatus.Rescheduled ) {
            final MaintenanceNotification notification = reservation.getNotification();
            if ( notification != null ) {
                pushNotificationSender.sendNotification(new MaintenanceNotificationRescheduleTemplate(notification), notification.getTargetApartment().getTenant());
            }
        }

        return reservation;
    }


    public List<MaintenanceReservation> findByPropertyId(Long propertyId, LocalDate date) {
        final Property property = propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }

        return reservationDao.findByPropertyAndEndTimeAfter(property, OffsetDateTime.now());
    }

    public List<MaintenanceReservation> findByTrainerId(Long employeeId, LocalDate date) {
        final ManagedEmployee employee = managedEmployeeDao.findById(employeeId);
        if ( employee == null ) {
            throw new EntityNotFoundException(String.format("Employee id=%d not found", employeeId));
        }

        return reservationDao.findByEmployeeAndEndTimeAfter(employee, OffsetDateTime.now());
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

        for ( SlotUnit unit : reservation.getUnits() ) {
            unit.setCapacity(unit.getCapacity() + reservation.getCapacity());
            slotUnitDao.persist(unit);
        }

        reservationDao.delete(reservation);
        return reservation;
    }

    @RoleSecured(AccountRole.Tenant)
    public MaintenanceReservation respondToReschedule(@NotNull Long reservationId, @NotNull RespondToRescheduleRequest request) {
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

        if ( request.getResponseType() == RespondToRescheduleRequest.RescheduleResponseType.Accept ) {
            reservation.setStatus(ReservationStatus.Confirmed);
        }
        else if ( request.getResponseType() == RespondToRescheduleRequest.RescheduleResponseType.Reject ) {
            reservation.setStatus(ReservationStatus.Rejected);
        }
        reservationDao.persist(reservation);

        return reservation;
    }
}
