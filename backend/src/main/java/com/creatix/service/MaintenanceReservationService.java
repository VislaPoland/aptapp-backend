package com.creatix.service;

import com.creatix.domain.dao.*;
import com.creatix.domain.dto.property.slot.PersistMaintenanceReservationRequest;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.MaintenanceSlot;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.SlotUnit;
import com.creatix.domain.entity.store.account.ManagedEmployee;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;

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

    @RoleSecured(AccountRole.Maintenance)
    public MaintenanceReservation createMaintenanceReservation(Long propertyId, PersistMaintenanceReservationRequest reservationRequest) {

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

        final MaintenanceReservation reservation = new MaintenanceReservation();
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
            reservation.getUnits().stream()
                    .forEach(unit -> {
                        unit.setCapacity(unit.getCapacity() - reservation.getCapacity());
                        if (unit.getCapacity() < 0) {
                            throw new IllegalArgumentException("Insufficient capacity");
                        }
                        slotUnitDao.persist(unit);
                    });
        }
        reservationDao.persist(reservation);

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
}
