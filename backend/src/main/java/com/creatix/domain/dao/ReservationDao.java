package com.creatix.domain.dao;

import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.ManagedEmployee;
import static com.creatix.domain.entity.store.QMaintenanceReservation.maintenanceReservation;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
@Transactional
public class ReservationDao extends DaoBase<MaintenanceReservation, Long> {

    public List<MaintenanceReservation> findByEmployeeAndEndTimeAfter(ManagedEmployee employee, OffsetDateTime endTime) {
        return queryFactory.selectFrom(maintenanceReservation)
                .where(maintenanceReservation.employee.eq(employee).and(maintenanceReservation.endTime.after(endTime)))
                .orderBy(maintenanceReservation.endTime.asc())
                .fetch();
    }

    public List<MaintenanceReservation> findByEmployeeAndEndTimeBefore(ManagedEmployee employee, OffsetDateTime endTime) {
        return queryFactory.selectFrom(maintenanceReservation)
                .where(maintenanceReservation.employee.eq(employee).and(maintenanceReservation.endTime.before(endTime)))
                .orderBy(maintenanceReservation.endTime.asc())
                .fetch();
    }

    public List<MaintenanceReservation> findByPropertyAndEndTimeAfter(Property property, OffsetDateTime endTime) {
        return queryFactory.selectFrom(maintenanceReservation)
                .where(maintenanceReservation.employee.manager.managedProperty.eq(property).and(maintenanceReservation.endTime.after(endTime)))
                .orderBy(maintenanceReservation.endTime.asc())
                .fetch();
    }

    public List<MaintenanceReservation> findByPropertyAndEndTimeBefore(Property property, OffsetDateTime endTime) {
        return queryFactory.selectFrom(maintenanceReservation)
                .where(maintenanceReservation.employee.manager.managedProperty.eq(property).and(maintenanceReservation.endTime.before(endTime)))
                .orderBy(maintenanceReservation.endTime.asc())
                .fetch();
    }
}
