package com.creatix.domain.dao;

import com.creatix.domain.entity.store.*;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.QMaintenanceNotification;
import com.creatix.domain.enums.AudienceType;
import com.creatix.domain.enums.ReservationStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.creatix.domain.entity.store.QSlot.slot;

@Repository
@Transactional
public class SlotDao extends DaoBase<Slot, Long> {

    public List<Slot> findAll() {
        return queryFactory.selectFrom(QSlot.slot).fetch();
    }

    public List<Slot> findByPropertyAndAccountAndDateRange(@Nonnull Property property, @Nonnull Account account, @Nonnull OffsetDateTime from, @Nonnull OffsetDateTime to) {
        Objects.requireNonNull(property, "Property is null");
        Objects.requireNonNull(from, "From date is null");
        Objects.requireNonNull(to, "To date is null");
        Objects.requireNonNull(account, "Account is null");

        final Environment env = createEnvironment(property, account);
        return env.query
                .where(env.predicate.and(slot.property.eq(property)).and(slot.beginTime.between(from, to)))
                .orderBy(slot.id.asc())
                .fetch();
    }

    public List<Slot> findByPropertyAndAccountAndSlotIdGreaterOrEqual(@Nonnull Property property, @Nonnull Account account, @Nonnull Long slotId, @Nonnull Integer pageSize) {
        Objects.requireNonNull(property, "Property is null");
        Objects.requireNonNull(slotId, "Slot id is null");
        Objects.requireNonNull(pageSize, "Page size is null");
        Objects.requireNonNull(account, "Account is null");

        final Environment env = createEnvironment(property, account);
        return env.query
                .where(env.predicate.and(slot.property.eq(property)).and(slot.id.goe(slotId)))
                .orderBy(slot.id.asc())
                .limit(pageSize)
                .fetch();
    }

    public List<Slot> findByPropertyAndAccountAndBeginTime(@Nonnull Property property, @Nonnull Account account, @Nonnull OffsetDateTime beginTime, @Nonnull Integer pageSize) {
        Objects.requireNonNull(property, "Property is null");
        Objects.requireNonNull(beginTime, "Begin time is null");
        Objects.requireNonNull(pageSize, "Page size is null");
        Objects.requireNonNull(account, "Account is null");

        final Environment env = createEnvironment(property, account);

        return env.query
                .where(env.predicate.and(slot.beginTime.eq(beginTime).or(slot.beginTime.after(beginTime))))
                .orderBy(slot.id.asc())
                .limit(pageSize)
                .fetch();
    }

    private Environment createEnvironment(@Nonnull Property property, @Nonnull Account account) {
        final Environment env = new Environment();
        env.slot = QSlot.slot;
        env.reservations = QMaintenanceReservation.maintenanceReservation;
        env.tenant = QTenant.tenant;
        env.employee = QManagedEmployee.managedEmployee;
        env.maintenanceNotification = QMaintenanceNotification.maintenanceNotification;;
        env.query = queryFactory.selectFrom(env.slot)
                .distinct()
                .leftJoin(env.slot.as(QMaintenanceSlot.class).reservations, env.reservations)
                .leftJoin(env.reservations.employee, env.employee)
                .leftJoin(env.reservations.notification, env.maintenanceNotification)
                .leftJoin(env.maintenanceNotification.targetApartment.tenant, env.tenant);

        env.predicate = env.slot.property.eq(property);

        if ( (account instanceof MaintenanceEmployee) || (account instanceof PropertyManager) || (account instanceof AssistantPropertyManager) ) {
            // all reservations in state pending or state confirmed or events
            env.predicate = env.predicate.and(env.reservations.status.in(ReservationStatus.Pending, ReservationStatus.Confirmed).or(env.slot.instanceOf(EventSlot.class)));
        }
        else if ( account instanceof SecurityEmployee ) {
            // filter: self created maintenance notification or events
            env.predicate = env.predicate.and(env.maintenanceNotification.author.id.eq(account.getId()).or(env.slot.instanceOf(EventSlot.class)));
        }
        else if ( account instanceof Tenant ) {
            // filter: tenant's maintenance or events
            env.predicate = env.predicate.and(env.tenant.id.eq(account.getId()).or(env.slot.instanceOf(EventSlot.class)));
        }
        else {
            env.predicate = env.predicate.and(env.slot.instanceOf(EventSlot.class));
        }


        // allow to see only appropriate events (filter by audience)
        if ( account instanceof ManagedEmployee ) {
            env.predicate = env.predicate.and(env.slot.instanceOf(MaintenanceSlot.class)
                    .or(env.slot.as(QEventSlot.class).audience.eq(AudienceType.Everyone))
                    .or(env.slot.as(QEventSlot.class).audience.eq(AudienceType.Employees)));
        }
        else if ( account instanceof TenantBase ) {
            env.predicate = env.predicate.and(env.slot.instanceOf(MaintenanceSlot.class)
                    .or(env.slot.as(QEventSlot.class).audience.eq(AudienceType.Everyone))
                    .or(env.slot.as(QEventSlot.class).audience.eq(AudienceType.Tenants)));
        }

        return env;
    }

    private static final class Environment {
        JPQLQuery<Slot> query;
        QMaintenanceReservation reservations;
        QTenant tenant;
        QManagedEmployee employee;
        BooleanExpression predicate;
        QSlot slot;
        QMaintenanceNotification maintenanceNotification;
    }

}
