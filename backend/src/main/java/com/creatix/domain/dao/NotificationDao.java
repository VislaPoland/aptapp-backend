package com.creatix.domain.dao;

import com.creatix.domain.entity.Notification;
import com.creatix.domain.entity.QNotification;
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


}
