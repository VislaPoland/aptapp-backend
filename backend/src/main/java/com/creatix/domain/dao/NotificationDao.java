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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Repository
@Transactional
public class NotificationDao extends AbstractNotificationDao<Notification> {

    @Autowired
    private AuthorizationManager authorizationManager;


    public List<Notification> findReceivedBusinessProfileNotifications(@Nullable Long startId,
                                                                       @NotNull Account account,
                                                                       int pageSize) {
        final QNotification qNotification = QNotification.notification;
        return queryFactory.selectFrom(qNotification)
                .where()
                .orderBy(qNotification.updatedAt.desc())
                .limit(pageSize)
                .fetch();
    }
    public List<Notification> findReceivedCommunityBoardCommentNotifications(@Nullable Long startId,
                                                                             @NotNull Account account,
                                                                             int pageSize) {
        final QNotification qNotification = QNotification.notification;
        //Find all notifications about comments, that are linked to community board items created by me
        BooleanExpression predicate = qNotification.as(QCommentNotification.class).communityBoardComment.communityBoardItem.account.eq(account);
        return queryFactory.selectFrom(qNotification)
                .where(predicate)
                .orderBy(qNotification.updatedAt.desc())
                .limit(pageSize)
                .fetch();
    }
    public List<Notification> findReceivedPersonalMessageNotifications(@Nullable Long startId,
                                                                       @NotNull Account account,
                                                                       int pageSize) {
        final QNotification qNotification = QNotification.notification;
        // Find all notification items that have linked personal message to my account
        BooleanExpression predicate = qNotification.as(QPersonalMessageNotification.class).personalMessage.toAccount.eq(account);
        return queryFactory.selectFrom(qNotification)
                .where(predicate)
                .orderBy(qNotification.updatedAt.desc())
                .limit(pageSize)
                .fetch();
    }


    public List<Notification> findPageByNotificationStatusAndNotificationTypeAndRequestTypeAndAccount(
            @NotNull NotificationRequestType requestType,
            @Nullable NotificationStatus notificationStatus,
            @Nullable List<NotificationType> notificationTypeList,
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

            switch (account.getRole()) {
                case Tenant:
                case SubTenant:
                    predicate = predicate.and(
                            qNotification.instanceOfAny(
                                    BusinessProfileNotification.class,
                                    DiscountCouponNotification.class
                            ).and(
                                    qNotification.property.eq(
                                            authorizationManager.getCurrentProperty(account)
                                    )
                            )
                    ).or(
                            qNotification.recipient.eq(account)
                    );
                    break;
                case PropertyManager:
                case AssistantPropertyManager:
                    predicate = predicate.and(
                            qNotification.property.eq(
                                    authorizationManager.getCurrentProperty(account)
                            ).or(
                                    qNotification.recipient.eq(authorizationManager.getCurrentAccount())
                            )
                    );
                    break;
                case Maintenance:
                    predicate = predicate.and(qNotification.instanceOf(MaintenanceNotification.class)).and(
                            qNotification.property.eq(
                                    authorizationManager.getCurrentProperty(account)
                            ).or(
                                    qNotification.recipient.eq(authorizationManager.getCurrentAccount())
                            )
                    );
                    break;
                case Security:
                    predicate = predicate.and(qNotification.instanceOf(SecurityNotification.class)).and(
                            qNotification.property.eq(
                                    authorizationManager.getCurrentProperty(account)
                            ).or(
                                    qNotification.recipient.eq(authorizationManager.getCurrentAccount())
                            )
                    );
                    break;
            }
        }


        if ( notificationStatus != null ) {
            predicate = predicate.and(qNotification.status.eq(notificationStatus));
        }


        if (null != notificationTypeList) {
            Optional<BooleanExpression> reduce = notificationTypeList
                .parallelStream()
                .map(
                    nt -> {
                        switch (nt) {
                            case BusinessProfile:
                                return BusinessProfileNotification.class;
                            case Comment:
                                return CommentNotification.class;
                            case CommunityBoardItemUpdatedSubscriber:
                                return CommunityBoardItemUpdatedSubscriberNotification.class;
                            case DiscountCoupon:
                                return DiscountCouponNotification.class;
                            case Maintenance:
                                return MaintenanceNotification.class;
                            case Neighborhood:
                                return NeighborhoodNotification.class;
                            case PersonalMessage:
                                return PersonalMessageNotification.class;
                            case Security:
                                return SecurityNotification.class;
                            default:
                                return null;
                        }
                    }
                )
                .map(qNotification::instanceOf)
                .reduce(BooleanExpression::or);
            if (reduce.isPresent()) {
                predicate = predicate.and(reduce.get());
            }
        }

        return queryFactory.selectFrom(qNotification)
                .where(predicate)
                .orderBy(qNotification.updatedAt.desc())
                .limit(pageSize)
                .fetch();
    }
}
