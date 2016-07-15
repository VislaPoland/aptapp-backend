package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.NotificationRequestType;

import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class NotificationDao extends AbstractNotificationDao<Notification> {


    public List<Notification> findPageByNotificationStatusAndNotificationTypeAndRequestTypeAndAccount(
            @NotNull NotificationRequestType requestType,
            @Nullable NotificationStatus notificationStatus,
            @Nullable NotificationType notificationType,
            @Nullable Long startId,
            @NotNull Account account,
            int pageSize) {

        final QNotification qNotification = QNotification.notification;

        BooleanExpression predicate;

        if ( startId == null ) {
            predicate = qNotification.createdAt.before(Date.from(Instant.now()));
        }
        else {
            predicate = qNotification.id.loe(startId);
        }


        if ( requestType == NotificationRequestType.Sent ) {
            predicate = predicate.and(qNotification.author.eq(account));
        }
        else if ( requestType == NotificationRequestType.Received ) {

            if ( account instanceof Tenant ) {
                // tenant is recipient
                predicate = predicate.and(qNotification.as(QNeighborhoodNotification.class).targetApartment.tenant.id.eq(account.getId()));
            }
            if ( account instanceof MaintenanceEmployee ) {
                // maintenance is recipient
                predicate = predicate.and(qNotification.instanceOf(MaintenanceNotification.class));
            }
            else if ( account instanceof SecurityEmployee ) {
                // security is recipient
                predicate = predicate.and(qNotification.instanceOf(SecurityNotification.class));
            }
        }


        if ( notificationStatus != null ) {
            predicate = predicate.and(qNotification.status.eq(notificationStatus));
        }


        if ( notificationType == NotificationType.Maintenance ) {
            predicate = predicate.and(qNotification.instanceOf(MaintenanceNotification.class));
        }
        else if ( notificationType == NotificationType.Security ) {
            predicate = predicate.and(qNotification.instanceOf(SecurityNotification.class));
        }
        else if ( notificationType == NotificationType.Neighborhood ) {
            predicate = predicate.and(qNotification.instanceOf(NeighborhoodNotification.class));
        }


        return queryFactory.selectFrom(qNotification)
                .where(predicate)
                .orderBy(qNotification.id.desc())
                .limit(pageSize)
                .fetch();
    }

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
