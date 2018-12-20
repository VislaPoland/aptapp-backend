package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.security.AuthorizationManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.List;

import static com.creatix.domain.entity.store.notification.QNeighborhoodNotification.neighborhoodNotification;

@Repository
@Transactional
public class NeighborhoodNotificationDao extends AbstractNotificationDao<NeighborhoodNotification> {

    public NeighborhoodNotificationDao(AuthorizationManager authorizationManager, NotificationGroupDao notificationGroupDao, NotificationHistoryDao notificationHistoryDao) {
        super(authorizationManager, notificationGroupDao, notificationHistoryDao);
    }

    @Nonnull
    public List<NeighborhoodNotification> findByCreatedAtBetween(@Nonnull OffsetDateTime dateFrom, @Nonnull OffsetDateTime dateTo) {
        return queryFactory.selectFrom(neighborhoodNotification)
                .where(neighborhoodNotification.createdAt.between(dateFrom, dateTo))
                .orderBy(neighborhoodNotification.createdAt.asc())
                .fetch();
    }
}
