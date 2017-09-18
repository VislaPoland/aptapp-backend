package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.entity.store.notification.QMaintenanceNotification;
import com.creatix.domain.enums.NotificationRequestType;
import com.creatix.domain.enums.NotificationStatus;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class MaintenanceNotificationDao extends AbstractNotificationDao<MaintenanceNotification> {
    public List<MaintenanceNotification> findAllInDateRange(OffsetDateTime fromDate, OffsetDateTime tillDate) {
        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;
        return queryFactory.selectFrom(maintenanceNotification)
                .where(maintenanceNotification.createdAt.between(fromDate, tillDate))
                .fetch();
    }

    private Predicate filtersPredicate(@Nonnull QMaintenanceNotification maintenanceNotification, NotificationStatus status,
                                       @Nonnull NotificationRequestType type, @Nonnull Account a) {
        final BooleanExpression predicate = (status != null ? maintenanceNotification.status.eq(status) : maintenanceNotification.status.isNotNull());
        switch (type) {
            case Sent:
                switch ( a.getRole() ) {
                    case SubTenant:
                        SubTenant subTenant = (SubTenant) a;
                        return predicate.and(
                                maintenanceNotification.author.eq(a)
                                .or(maintenanceNotification.targetApartment.tenant.subTenants.any().eq(subTenant)));
                    default:
                        return predicate.and(maintenanceNotification.author.eq(a));
                }
            case Received:
                switch (a.getRole()) {
                    case PropertyManager:
                        return predicate.and(maintenanceNotification.property.eq(((PropertyManager) a).getManagedProperty()));
                    case AssistantPropertyManager:
                        return predicate.and(maintenanceNotification.property.eq(((AssistantPropertyManager) a).getManager().getManagedProperty()));
                    case Maintenance:
                        return predicate.and(maintenanceNotification.property.eq(((MaintenanceEmployee) a).getManager().getManagedProperty()));
                    case Tenant:
                    case SubTenant:
                        return predicate.and(maintenanceNotification.author.eq(a));
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    public long countByStatusAndType(NotificationStatus status, @Nonnull NotificationRequestType type, @Nonnull Account account) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(account);

        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;

        Predicate predicate = filtersPredicate(maintenanceNotification, status, type, account);
        if (predicate == null)
            return 0;

        return queryFactory.selectFrom(maintenanceNotification)
                .where(predicate)
                .fetchCount();
    }

    public List<MaintenanceNotification> findPageByStatusAndType(NotificationStatus status, @Nonnull NotificationRequestType type,
                                                                 @Nonnull Account account, long pageNumber, long pageSize) {
        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;

        Predicate predicate = filtersPredicate(maintenanceNotification, status, type, account);
        if (predicate == null)
            return Collections.emptyList();

        return queryFactory.selectFrom(maintenanceNotification)
                .where(predicate)
                .orderBy(maintenanceNotification.createdAt.desc())
                .limit(pageNumber * pageSize + pageSize)
                .offset(pageNumber * pageSize)
                .fetch();
    }
}
