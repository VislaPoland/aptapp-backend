package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.EscalatedNeighborhoodNotification;
import com.creatix.domain.entity.store.notification.NotificationGroup;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.List;

import static com.creatix.domain.entity.store.notification.QEscalatedNeighborhoodNotification.escalatedNeighborhoodNotification;

@Repository
@Transactional
public class EscalatedNeighborhoodNotificationDao extends AbstractNotificationDao<EscalatedNeighborhoodNotification> {

    @Nonnull
    public List<EscalatedNeighborhoodNotification> findByCreatedAtBetween(@Nonnull OffsetDateTime dateFrom, @Nonnull OffsetDateTime dateTo) {
        return queryFactory.selectFrom(escalatedNeighborhoodNotification)
                .where(escalatedNeighborhoodNotification.createdAt.between(dateFrom, dateTo))
                .orderBy(escalatedNeighborhoodNotification.createdAt.asc())
                .fetch();
    }

    public List<EscalatedNeighborhoodNotification> findByNotificationGroup(@Nonnull long notificationId) {
        return queryFactory.selectFrom(escalatedNeighborhoodNotification)
                .where(escalatedNeighborhoodNotification.notificationGroup.eq(
                        queryFactory.selectFrom(escalatedNeighborhoodNotification)
                        .where(escalatedNeighborhoodNotification.id.eq(notificationId))
                        .fetchFirst().notificationGroup))
                .fetch();
    }
}
