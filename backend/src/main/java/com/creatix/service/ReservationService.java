package com.creatix.service;

import com.creatix.domain.dao.*;
import com.creatix.domain.dto.property.slot.PersistReservationRequest;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.Reservation;
import com.creatix.domain.entity.Slot;
import com.creatix.domain.entity.SlotUnit;
import com.creatix.domain.entity.account.ManagedEmployee;
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
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private static final Object syncLock = new Object();

    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private SlotDao slotDao;
    @Autowired
    private SlotService slotService;
    @Autowired
    private SlotUnitDao slotUnitDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private ManagedEmployeeDao managedEmployeeDao;

    @RoleSecured({AccountRole.Security, AccountRole.Maintenance})
    public Reservation create(@SuppressWarnings("unused") long trainerId, PersistReservationRequest reservationRequest) {

        final Slot slot = slotDao.findById(reservationRequest.getSlotId());
        if ( slot == null ) {
            throw new EntityNotFoundException(String.format("Slot id=%d not found", reservationRequest.getSlotId()));
        }

        final ManagedEmployee employee = (ManagedEmployee) authorizationManager.getCurrentAccount();
        if ( employee == null ) {
            throw new IllegalStateException("Account is not employee assigned");
        }
        if ( reservationRequest.getBeginTime().isBefore(LocalTime.now()) ) {
            throw new IllegalArgumentException("Cannot create reservation in the past");
        }

        final int unitCount = slotService.calculateUnitCount(reservationRequest.getDurationMinutes(), slot.getUnitDurationMinutes());
        if ( unitCount == 0 ) {
            throw new IllegalArgumentException("Invalid duration of zero minutes");
        }

        final Reservation reservation = new Reservation();
        reservation.setDurationMinutes(reservationRequest.getDurationMinutes());
        reservation.setBeginTime(slot.getBeginTime().with(reservationRequest.getBeginTime()));
        reservation.setEndTime(reservation.getBeginTime().plusMinutes(slot.getUnitDurationMinutes()));
        reservation.setCapacity(reservationRequest.getCapacity());
        reservation.setSlot(slot);
        reservation.setEmployee(employee);
        reservation.setNote(reservationRequest.getNote());

        synchronized ( syncLock ) {
            // assign slots units to reservation
            final int unitOffsetLft = slotService.calculateUnitOffset(slot, reservation.getBeginTime());
            final int unitOffsetRgt = unitOffsetLft + unitCount - 1;
            slot.getUnits().stream()
                    .filter(u -> ((unitOffsetLft <= u.getOffset()) && (u.getOffset() <= unitOffsetRgt)))
                    .forEach(reservation::addUnit);
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


    public List<Reservation> findByPropertyId(Long propertyId, LocalDate date) {
        final Property property = propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }

        return reservationDao.findByPropertyAndEndTimeAfter(property, OffsetDateTime.now());
    }

    public List<Reservation> findByTrainerId(Long employeeId, LocalDate date) {
        final ManagedEmployee employee = managedEmployeeDao.findById(employeeId);
        if ( employee == null ) {
            throw new EntityNotFoundException(String.format("Employee id=%d not found", employeeId));
        }

        return reservationDao.findByEmployeeAndEndTimeAfter(employee, OffsetDateTime.now());
    }

    public Reservation deleteById(long reservationId) {
        final Reservation reservation = reservationDao.findById(reservationId);
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
