package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.NotificationRequestType;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.creatix.security.AuthorizationManager;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class NotificationDao extends AbstractNotificationDao<Notification> {

    @Autowired
    private AuthorizationManager authorizationManager;


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

        final QPersonalMessageGroup qPersonalMessageGroup = QPersonalMessageGroup.personalMessageGroup;
        final QPersonalMessage qPersonalMessage = QPersonalMessage.personalMessage;
        final QSubTenant qSubTenantAuthor = QSubTenant.subTenant;
        final QTenant qParentTenantOfSubTenantAuthor = QTenant.tenant;
        final QSubTenant qSubTenantRecipient = QSubTenant.subTenant;
        final QTenant qParentTenantOfSubTenantRecipient = QTenant.tenant;


        BooleanExpression predicate = qNotification.updatedAt.after(startDt).not();


        if ( requestType == NotificationRequestType.Sent ) {
            switch ( account.getRole() ) {
                case Tenant:
                    final Tenant tenant = (Tenant) account;
                    predicate = predicate.andAnyOf(
                            qNotification.author.eq(account),
                            qParentTenantOfSubTenantAuthor.subTenants.any().parentTenant.eq(tenant)
                    );
                    break;
                case SubTenant:
                    final SubTenant subTenant = (SubTenant) account;
                    predicate = predicate.andAnyOf(
                            qNotification.author.eq(account),                                   // sender is user itself
                            qParentTenantOfSubTenantAuthor.subTenants.any().eq(subTenant),      // sender is one the subtenants
                            qNotification.author.eq(subTenant.getParentTenant())                // sender is parent of the subtenant
                    );
                    break;
                default:
                    predicate = predicate.and(qNotification.author.eq(account));
                    break;
            }
        }
        else if ( requestType == NotificationRequestType.Received ) {

            switch (account.getRole()) {
                case Tenant:
                    final Tenant tenant = (Tenant) account;
                    predicate = predicate.andAnyOf(
                            Expressions.allOf(
                                    qNotification.instanceOfAny(BusinessProfileNotification.class, DiscountCouponNotification.class),
                                    qNotification.property.eq(authorizationManager.getCurrentProperty(account))
                            ),
                            qNotification.recipient.eq(account),
                            qParentTenantOfSubTenantRecipient.subTenants.any().parentTenant.eq(tenant),
                            qPersonalMessage.toAccount.eq(account)
                    );
                    break;
                case SubTenant:
                    final SubTenant subTenant = (SubTenant) account;
                    predicate = predicate.andAnyOf(
                            Expressions.allOf(
                                    qNotification.instanceOfAny(BusinessProfileNotification.class, DiscountCouponNotification.class),
                                    qNotification.property.eq(authorizationManager.getCurrentProperty(account))
                            ),
                            qNotification.recipient.eq(account),
                            qPersonalMessage.toAccount.eq(account),
                            qParentTenantOfSubTenantRecipient.subTenants.any().eq(subTenant),
                            qNotification.recipient.eq(subTenant.getParentTenant())
                    );
                    break;
                case PropertyManager:
                case AssistantPropertyManager:
                    predicate = predicate.and(
                            qNotification.property.eq(
                                    authorizationManager.getCurrentProperty(account)
                            )
                            .or(
                                    qNotification.recipient.eq(authorizationManager.getCurrentAccount())
                            )
                    );
                    break;
                case Maintenance:
                    predicate = predicate.and(qNotification.instanceOf(MaintenanceNotification.class)).and(
                            qNotification.property.eq(
                                    authorizationManager.getCurrentProperty(account)
                            )
                            .or(
                                    qNotification.recipient.eq(authorizationManager.getCurrentAccount())
                            )
                    );
                    break;
                case Security:
                    predicate = predicate.and(qNotification.instanceOf(SecurityNotification.class)).and(
                            qNotification.property.eq(
                                    authorizationManager.getCurrentProperty(account)
                            )
                            .or(
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
                .stream()
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
                .distinct()
                .leftJoin(qNotification.as(QPersonalMessageNotification.class).personalMessageGroup, qPersonalMessageGroup)
                .leftJoin(qPersonalMessageGroup.messages, qPersonalMessage)
                .leftJoin(qNotification.author.as(QSubTenant.class), qSubTenantAuthor)
                .leftJoin(qSubTenantAuthor.parentTenant, qParentTenantOfSubTenantAuthor)
                .leftJoin(qNotification.recipient.as(QSubTenant.class), qSubTenantRecipient)
                .leftJoin(qSubTenantRecipient.parentTenant, qParentTenantOfSubTenantRecipient)
                .where(predicate)
                .orderBy(qNotification.updatedAt.desc())
                .limit(pageSize)
                .fetch();
    }
}
