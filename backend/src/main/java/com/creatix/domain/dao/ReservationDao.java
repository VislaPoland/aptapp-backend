package com.creatix.domain.dao;

import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.Reservation;
import com.creatix.domain.entity.account.ManagedEmployee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static com.creatix.domain.entity.QReservation.reservation;

@Repository
@Transactional
public class ReservationDao extends DaoBase<Reservation, Long> {

    public List<Reservation> findByEmployeeAndEndTimeAfter(ManagedEmployee employee, OffsetDateTime endTime) {
        return queryFactory.selectFrom(reservation)
                .where(reservation.employee.eq(employee).and(reservation.endTime.after(endTime)))
                .orderBy(reservation.endTime.asc())
                .fetch();
    }

    public List<Reservation> findByEmployeeAndEndTimeBefore(ManagedEmployee employee, OffsetDateTime endTime) {
        return queryFactory.selectFrom(reservation)
                .where(reservation.employee.eq(employee).and(reservation.endTime.before(endTime)))
                .orderBy(reservation.endTime.asc())
                .fetch();
    }

    public List<Reservation> findByPropertyAndEndTimeAfter(Property property, OffsetDateTime endTime) {
        return queryFactory.selectFrom(reservation)
                .where(reservation.employee.manager.managedProperty.eq(property).and(reservation.endTime.after(endTime)))
                .orderBy(reservation.endTime.asc())
                .fetch();
    }

    public List<Reservation> findByPropertyAndEndTimeBefore(Property property, OffsetDateTime endTime) {
        return queryFactory.selectFrom(reservation)
                .where(reservation.employee.manager.managedProperty.eq(property).and(reservation.endTime.before(endTime)))
                .orderBy(reservation.endTime.asc())
                .fetch();
    }
}
