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
        final QSlot slot = QSlot.slot;
        final QMaintenanceReservation maintenanceReservations = QMaintenanceReservation.maintenanceReservation;
        final QManagedEmployee maintenanceEmployee = QManagedEmployee.managedEmployee;
        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;;
        final QSubTenant maintenanceSubTenantAuthor = QSubTenant.subTenant;
        final QTenant maintenanceParentTenantOfSubTenantAuthor = QTenant.tenant;

        env.query = queryFactory.selectFrom(slot)
                .distinct()
                .leftJoin(slot.as(QMaintenanceSlot.class).reservations, maintenanceReservations)
                .leftJoin(maintenanceReservations.employee, maintenanceEmployee)
                .leftJoin(maintenanceReservations.notification, maintenanceNotification)
                .leftJoin(maintenanceNotification.author.as(QSubTenant.class), maintenanceSubTenantAuthor)
                .leftJoin(maintenanceSubTenantAuthor.parentTenant, maintenanceParentTenantOfSubTenantAuthor);

        env.predicate = slot.property.eq(property);

        switch (account.getRole()) {
            case Maintenance:
            case PropertyManager:
            case AssistantPropertyManager:
            case Administrator:
                // all reservations in state pending or state confirmed or events
                env.predicate = env.predicate.and(maintenanceReservations.status.in(ReservationStatus.Pending, ReservationStatus.Confirmed).or(slot.instanceOf(EventSlot.class)));
                break;
            case Security:
                // filter: self created maintenance notification or events
                env.predicate = env.predicate.and(maintenanceNotification.author.id.eq(account.getId()).or(slot.instanceOf(EventSlot.class)));
                break;
            case Tenant:
                // filter: tenant's maintenance or events
                final Tenant tenant = (Tenant) account;
                env.predicate = env.predicate.andAnyOf(
                        slot.instanceOf(EventSlot.class),
                        maintenanceNotification.author.eq(tenant),
                        maintenanceParentTenantOfSubTenantAuthor.subTenants.any().parentTenant.eq(tenant));
                break;
            case SubTenant:
                // filter: tenant's maintenance or events
                final SubTenant subTenant = (SubTenant) account;
                env.predicate = env.predicate.andAnyOf(
                        slot.instanceOf(EventSlot.class),
                        maintenanceNotification.author.eq(account),
                        maintenanceNotification.author.eq(subTenant.getParentTenant()),
                        maintenanceParentTenantOfSubTenantAuthor.subTenants.any().eq(subTenant)
                );
                break;
            case PropertyOwner:
            default:
                env.predicate = env.predicate.and(slot.instanceOf(EventSlot.class));
                break;
        }

        // allow to see only appropriate events (filter by audience)
        if ( account instanceof ManagedEmployee ) {
            env.predicate = env.predicate.and(
                    slot.instanceOf(MaintenanceSlot.class)
                    .or(slot.as(QEventSlot.class).audience.eq(AudienceType.Everyone))
                    .or(slot.as(QEventSlot.class).audience.eq(AudienceType.Employees)));
        }
        else if ( account instanceof TenantBase ) {
            env.predicate = env.predicate.and(
                    slot.instanceOf(MaintenanceSlot.class)
                    .or(slot.as(QEventSlot.class).audience.eq(AudienceType.Everyone))
                    .or(slot.as(QEventSlot.class).audience.eq(AudienceType.Tenants)));
        }

        return env;
    }

    private static final class Environment {
        JPQLQuery<Slot> query;
        BooleanExpression predicate;
    }

}
