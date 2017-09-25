package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.domain.entity.store.notification.NotificationGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@Transactional
public abstract class AbstractNotificationDao<T extends Notification> extends DaoBase<T, Long> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotificationGroupDao notificationGroupDao;

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

}
