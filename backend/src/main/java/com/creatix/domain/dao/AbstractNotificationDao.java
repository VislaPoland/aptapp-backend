package com.creatix.domain.dao;

import com.creatix.domain.entity.Notification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Transactional
public abstract class AbstractNotificationDao<T extends Notification> extends DaoBase<T, Long> {

    @Override
    public void persist(T notification) {
        if ( notification.getCreatedAt() == null ) {
            notification.setCreatedAt(new Date());
            notification.setUpdatedAt(new Date());
        }
        else {
            notification.setUpdatedAt(new Date());
        }

        super.persist(notification);
    }

}
