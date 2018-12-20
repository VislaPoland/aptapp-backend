package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.domain.entity.store.notification.NotificationGroup;
import com.creatix.domain.entity.store.notification.NotificationHistory;
import com.creatix.domain.enums.NotificationHistoryStatus;
import com.creatix.security.AuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;


@RequiredArgsConstructor
public abstract class AbstractNotificationDao<T extends Notification> extends DaoBase<T, Long> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final AuthorizationManager authorizationManager;
    private final NotificationGroupDao notificationGroupDao;
    private final NotificationHistoryDao notificationHistoryDao;

    @Override
    public void persist(T notification) {
        if ( notification.getCreatedAt() == null ) {
            notification.setCreatedAt(OffsetDateTime.now());
            notification.setUpdatedAt(OffsetDateTime.now());
        }
        else {
            notification.setUpdatedAt(OffsetDateTime.now());
        }

        boolean defaultGroupCreated = false;
        if ( notification.getNotificationGroup() == null ) {
            final NotificationGroup group = new NotificationGroup();
            group.addNotification(notification);
            notificationGroupDao.persist(group);
            defaultGroupCreated = true;
        }

        super.persist(notification);

        if ( defaultGroupCreated ) {
            logger.info("Created default group for notification, id={}", notification.getId());
        }
    }

    public void createNotificationHistoryLog(T notification, String status) {
        final NotificationHistory historyRecord = new NotificationHistory();
        historyRecord.setAuthor(authorizationManager.getCurrentAccount());
        historyRecord.setNotification(notification);
        historyRecord.setStatus(NotificationHistoryStatus.valueOf(status));

        notificationHistoryDao.persist(historyRecord);
    }

}
