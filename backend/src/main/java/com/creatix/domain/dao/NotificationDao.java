package com.creatix.domain.dao;

import com.creatix.domain.dto.notification.NotificationRequestType;

import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.domain.entity.store.notification.QMaintenanceNotification;
import com.creatix.domain.entity.store.notification.QNeighborhoodNotification;
import com.creatix.domain.entity.store.notification.QNotification;
import com.creatix.domain.enums.NotificationType;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class NotificationDao extends AbstractNotificationDao<Notification> {

    public long countByType(NotificationRequestType type, Account account) {
        final QNotification notification = QNotification.notification;

        return queryFactory.selectFrom(notification)
                .where(createNotificationFilterPredicateForAccount(notification, type, account))
                .fetchCount();
    }

    public List<Notification> findPageByType(NotificationRequestType type, Account account, long pageNumber, long pageSize) {
        final QNotification notification = QNotification.notification;

        return queryFactory.selectFrom(notification)
                .where(createNotificationFilterPredicateForAccount(notification, type, account))
                .orderBy(notification.createdAt.desc())
                .limit(pageNumber * pageSize + pageSize)
                .offset(pageNumber * pageSize)
                .fetch();
    }


    private static Predicate createNotificationFilterPredicateForAccount(final QNotification notification, NotificationRequestType type, Account a) {
        switch ( type ) {
            case Sent:
                return notification.author.eq(a);
            case Received:
                switch ( a.getRole() ) {
                    case PropertyManager:
                        return notification.property.eq(((PropertyManager) a).getManagedProperty());
                    case AssistantPropertyManager:
                        return notification.property.eq(((AssistantPropertyManager) a).getManager().getManagedProperty());
                    case Maintenance:
                        return notification.type.eq(NotificationType.Maintenance)
                                .and(notification.property.eq(((MaintenanceEmployee) a).getManager().getManagedProperty()));
                    case Security:
                        return notification.type.eq(NotificationType.Security)
                                .and(notification.property.eq(((SecurityEmployee) a).getManager().getManagedProperty()));
                    case Tenant:
                        final QMaintenanceNotification maintenanceNotification = notification.as(QMaintenanceNotification.class);
                        final QNeighborhoodNotification neighborhoodNotification = notification.as(QNeighborhoodNotification.class);

                        return maintenanceNotification.targetApartment.eq(((Tenant) a).getApartment())
                                .or(neighborhoodNotification.targetApartment.eq(((Tenant) a).getApartment()));
                    default:
                        throw new IllegalStateException(String.format("Account type %s is not allowed to read maintenance notifications", a.getRole().toString()));
                }
            default:
                throw new IllegalStateException();
        }
    }
}
