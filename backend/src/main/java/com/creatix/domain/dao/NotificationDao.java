package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Notification;
import com.creatix.domain.entity.store.QNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class NotificationDao extends AbstractNotificationDao<Notification> {

    public List<Notification> findAllInDateRange(Date fromDate, Date tillDate) {
        final QNotification notification = QNotification.notification;
        return queryFactory.selectFrom(notification)
                .where(notification.date.between(fromDate, tillDate))
                .fetch();
    }

    //TODO update to more effective query
    public List<Notification> findAll() {
        final QNotification notification = QNotification.notification;
        return queryFactory.selectFrom(notification)
                .fetch();
    }

}
