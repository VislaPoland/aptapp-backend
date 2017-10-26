package com.creatix.service.notification;

import com.creatix.domain.dao.EscalatedNeighborhoodNotificationDao;
import com.creatix.domain.dao.NeighborhoodNotificationDao;
import com.creatix.domain.entity.store.notification.EscalatedNeighborhoodNotification;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by Tomas Sedlak on 26.10.2017.
 */
@Component
public class NotificationsStatisticsCreator {

    @Nonnull
    private final EscalatedNeighborhoodNotificationDao escNbrNotificationDao;
    @Nonnull
    private final NeighborhoodNotificationDao nbrNotificationDao;

    @Autowired
    public NotificationsStatisticsCreator(@Nonnull NeighborhoodNotificationDao nbrNotificationDao, @Nonnull EscalatedNeighborhoodNotificationDao escNbrNotificationDao) {
        this.nbrNotificationDao = nbrNotificationDao;
        this.escNbrNotificationDao = escNbrNotificationDao;
    }

    public NotificationsStatistics createForTimeRange(@Nonnull OffsetDateTime dateFrom, @Nonnull OffsetDateTime dateTo) {

        final NotificationsStatistics statistics = new NotificationsStatistics();
        statistics.setDateFrom(dateFrom);
        statistics.setDateTo(dateTo);

        final List<NeighborhoodNotification> neighborhoodNotifications = nbrNotificationDao.findByCreatedAtBetween(dateFrom, dateTo);
        statistics.setNotificationCount(neighborhoodNotifications.size());

        final List<EscalatedNeighborhoodNotification> escalatedNotifications = escNbrNotificationDao.findByCreatedAtBetween(dateFrom, dateTo);
        statistics.setEscalationCount(escalatedNotifications.size());

        return statistics;
    }


}
