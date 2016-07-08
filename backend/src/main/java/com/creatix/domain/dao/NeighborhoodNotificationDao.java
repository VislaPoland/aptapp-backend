package com.creatix.domain.dao;

import com.creatix.domain.dto.notification.NotificationRequestType;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.AssistantPropertyManager;
import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.entity.store.notification.QNeighborhoodNotification;
import com.creatix.domain.enums.NotificationStatus;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Repository
@Transactional
public class NeighborhoodNotificationDao extends AbstractNotificationDao<NeighborhoodNotification> {
    private Predicate filtersPredicate(final QNeighborhoodNotification neighborhoodNotification, NotificationStatus status,
                                       NotificationRequestType type, Account a) {
        final BooleanExpression predicate = neighborhoodNotification.status.eq(status);
        switch (type) {
            case Sent:
                return predicate.and(neighborhoodNotification.author.eq(a));
            case Received:
                switch (a.getRole()) {
                    case PropertyManager:
                        return predicate.and(neighborhoodNotification.property.eq(((PropertyManager) a).getManagedProperty()));
                    case AssistantPropertyManager:
                        return predicate.and(neighborhoodNotification.property.eq(((AssistantPropertyManager) a).getManager().getManagedProperty()));
                    case Tenant:
                        return predicate.and(neighborhoodNotification.targetApartment.eq(((Tenant) a).getApartment()));
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    public long countByStatusAndType(NotificationStatus status, NotificationRequestType type, Account account) {
        final QNeighborhoodNotification neighborhoodNotification = QNeighborhoodNotification.neighborhoodNotification;

        Predicate predicate = filtersPredicate(neighborhoodNotification, status, type, account);
        if (predicate == null)
            return 0;

        return queryFactory.selectFrom(neighborhoodNotification)
                .where(predicate)
                .fetchCount();
    }

    public List<NeighborhoodNotification> findPageByStatusAndType(NotificationStatus status, NotificationRequestType type, Account account, long pageNumber, long pageSize) {
        final QNeighborhoodNotification neighborhoodNotification = QNeighborhoodNotification.neighborhoodNotification;

        Predicate predicate = filtersPredicate(neighborhoodNotification, status, type, account);
        if (predicate == null)
            return Collections.emptyList();

        return queryFactory.selectFrom(neighborhoodNotification)
                .where(predicate)
                .orderBy(neighborhoodNotification.createdAt.desc())
                .limit(pageNumber * pageSize + pageSize)
                .offset(pageNumber * pageSize)
                .fetch();
    }
}
