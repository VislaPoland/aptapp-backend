package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.MaintenanceEmployee;
import com.creatix.domain.entity.store.account.SecurityEmployee;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.NotificationRequestType;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.creatix.security.AuthorizationManager;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.OffsetDateTime;
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


        final OffsetDateTime startDt;
        if ( startId == null ) {
            startDt = OffsetDateTime.now();
        }
        else {
            final Notification startNotification = findById(startId);
            startDt = startNotification.getUpdatedAt();
        }

        BooleanExpression predicate = qNotification.updatedAt.after(startDt).not();


        if ( requestType == NotificationRequestType.Sent ) {
            predicate = predicate.and(qNotification.author.eq(account));
        }
        else if ( requestType == NotificationRequestType.Received ) {

            if ( account instanceof Tenant ) {
                // tenant is recipient
                predicate = predicate.and(qNotification.as(QNeighborhoodNotification.class).targetApartment.tenant.id.eq(account.getId())).and(qNotification.instanceOf(NeighborhoodNotification.class));
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

        predicate = predicate.and(qNotification.property.eq(AuthorizationManager.getCurrentProperty(account)));

        return queryFactory.selectFrom(qNotification)
                .where(predicate)
                .orderBy(qNotification.updatedAt.desc())
                .limit(pageSize)
                .fetch();
    }
}
