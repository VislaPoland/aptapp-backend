package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.Notification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Date;

@Component
@Transactional
public abstract class AbstractNotificationDao<T extends Notification> extends DaoBase<T, Long> {

    @Override
    public void persist(T notification) {
        if ( notification.getCreatedAt() == null ) {
            notification.setCreatedAt(OffsetDateTime.now());
            notification.setUpdatedAt(OffsetDateTime.now());
        }
        else {
            notification.setUpdatedAt(OffsetDateTime.now());
        }

        super.persist(notification);
    }

}
