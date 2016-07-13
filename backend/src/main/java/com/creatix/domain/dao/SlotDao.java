package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.QMaintenanceReservation;
import com.creatix.domain.entity.store.QMaintenanceSlot;
import com.creatix.domain.entity.store.Slot;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.MaintenanceEmployee;
import com.creatix.domain.entity.store.account.Tenant;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.creatix.domain.entity.store.QSlot.slot;

@Repository
@Transactional
public class SlotDao extends DaoBase<Slot, Long> {

    public List<Slot> findByPropertyAndDateRange(Property property, OffsetDateTime from, OffsetDateTime to) {
        return queryFactory.selectFrom(slot)
                .where(slot.property.eq(property).and(slot.beginTime.between(from, to)))
                .orderBy(slot.beginTime.asc())
                .fetch();
    }

    public List<Slot> findByPropertyAndSlotIdGreaterOrEqual(Property property, Long slotId, Integer pageSize) {
        return queryFactory.selectFrom(slot)
                .where(slot.property.eq(property).and(slot.id.goe(slotId)))
                .orderBy(slot.id.asc())
                .limit(pageSize)
                .fetch();
    }

    public List<Slot> findByPropertyAndBeginTime(Property property, OffsetDateTime beginTime, Integer pageSize) {
        return queryFactory.selectFrom(slot)
                .where(slot.property.eq(property).and(slot.beginTime.eq(beginTime).or(slot.beginTime.after(beginTime))))
                .orderBy(slot.beginTime.asc())
                .limit(pageSize)
                .fetch();
    }

    public List<Slot> findByPropertyAndBeginTimeAndAccount(@NotNull Property property, @NotNull OffsetDateTime beginTime, @NotNull Integer pageSize, @NotNull Account account) {
        Objects.requireNonNull(property, "Property is null");
        Objects.requireNonNull(beginTime, "Begin time is null");
        Objects.requireNonNull(pageSize, "Page size is null");
        Objects.requireNonNull(account, "Account is null");

        final QMaintenanceReservation reservations = QMaintenanceReservation.maintenanceReservation;
        final JPQLQuery<Slot> query = queryFactory.selectFrom(slot)
                .leftJoin(slot.as(QMaintenanceSlot.class).reservations, reservations);

        final BooleanExpression predicate = slot.property.eq(property).and(slot.beginTime.eq(beginTime).or(slot.beginTime.after(beginTime)));

        if ( account instanceof MaintenanceEmployee ) {
            predicate.and(reservations.employee.eq((MaintenanceEmployee) account).or(reservations.employee.isNull()));
        }
        else if ( account instanceof Tenant ) {
            predicate.and(reservations.notification.targetApartment.tenant.eq((Tenant) account).or(reservations.notification.targetApartment.tenant.isNull()));
        }

        return query.where(predicate)
                .orderBy(slot.beginTime.asc())
                .limit(pageSize)
                .fetch();
    }
}
