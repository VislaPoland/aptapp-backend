package com.creatix.domain.dao;

import com.creatix.domain.dto.notification.NotificationRequestType;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.QNeighborhoodNotification;
import com.creatix.domain.enums.NotificationStatus;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                        throw new IllegalStateException(String.format("Account type %s is not allowed to read maintenance notifications", a.getRole().toString()));
                }
            default:
                throw new IllegalStateException();
        }
    }

    public long countByStatusAndType(NotificationStatus status, NotificationRequestType type, Account account) {
        final QNeighborhoodNotification maintenanceNotification = QNeighborhoodNotification.neighborhoodNotification;

        return queryFactory.selectFrom(maintenanceNotification)
                .where(filtersPredicate(maintenanceNotification, status, type, account))
                .fetchCount();
    }

    public List<NeighborhoodNotification> findPageByStatusAndType(NotificationStatus status, NotificationRequestType type, Account account, long pageNumber, long pageSize) {
        final QNeighborhoodNotification neighborhoodNotification = QNeighborhoodNotification.neighborhoodNotification;

        return queryFactory.selectFrom(neighborhoodNotification)
                .where(filtersPredicate(neighborhoodNotification, status, type, account))
                .orderBy(neighborhoodNotification.createdAt.desc())
                .limit(pageNumber * pageSize + pageSize)
                .offset(pageNumber * pageSize)
                .fetch();
    }
}
