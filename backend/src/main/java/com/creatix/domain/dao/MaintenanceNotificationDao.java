package com.creatix.domain.dao;

import com.creatix.domain.dto.notification.NotificationRequestType;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.entity.store.notification.QMaintenanceNotification;
import com.creatix.domain.enums.NotificationStatus;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class MaintenanceNotificationDao extends AbstractNotificationDao<MaintenanceNotification> {
    public List<MaintenanceNotification> findAllInDateRange(Date fromDate, Date tillDate) {
        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;
        return queryFactory.selectFrom(maintenanceNotification)
                .where(maintenanceNotification.date.between(fromDate, tillDate))
                .fetch();
    }

    private Predicate filtersPredicate(final QMaintenanceNotification maintenanceNotification, NotificationStatus status,
                                       NotificationRequestType type, Account a) {
        final BooleanExpression predicate = maintenanceNotification.status.eq(status);
        switch (type) {
            case Sent:
                return predicate.and(maintenanceNotification.author.eq(a));
            case Received:
                switch (a.getRole()) {
                    case PropertyManager:
                        return predicate.and(maintenanceNotification.property.eq(((PropertyManager) a).getManagedProperty()));
                    case AssistantPropertyManager:
                        return predicate.and(maintenanceNotification.property.eq(((AssistantPropertyManager) a).getManager().getManagedProperty()));
                    case Maintenance:
                        return predicate.and(maintenanceNotification.property.eq(((MaintenanceEmployee) a).getManager().getManagedProperty()));
                    case Tenant:
                        return predicate.and(maintenanceNotification.targetApartment.eq(((Tenant) a).getApartment()));
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    public long countByStatusAndType(NotificationStatus status, NotificationRequestType type, Account account) {
        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;

        Predicate predicate = filtersPredicate(maintenanceNotification, status, type, account);
        if (predicate == null)
            return 0;

        return queryFactory.selectFrom(maintenanceNotification)
                .where(predicate)
                .fetchCount();
    }

    public List<MaintenanceNotification> findPageByStatusAndType(NotificationStatus status, NotificationRequestType type,
                                                                 Account account, long pageNumber, long pageSize) {
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
