package com.creatix.domain.dao;

import com.creatix.domain.entity.MaintenanceNotification;
import com.creatix.domain.entity.Notification;
import com.creatix.domain.entity.QMaintenanceNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class NotificationDao extends AbstractNotificationDao<Notification> {
    public List<Notification> findAllInDateRange(Date fromDate, Date tillDate) {
        return em.createQuery("SELECT n FROM Notification n WHERE n.date BETWEEN :fromDate AND :tillDate", Notification.class)
                .setParameter("fromDate", fromDate)
                .setParameter("tillDate", tillDate)
                .getResultList();
    }

    public List<MaintenanceNotification> findAllMaintenanceInDateRange(Date fromDate, Date tillDate) {
        return queryFactory.selectFrom(QMaintenanceNotification.maintenanceNotification)
                .where(QMaintenanceNotification.maintenanceNotification.date.between(fromDate, tillDate))
                .fetch();
    }
}
