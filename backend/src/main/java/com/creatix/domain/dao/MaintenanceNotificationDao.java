package com.creatix.domain.dao;

import com.creatix.domain.entity.MaintenanceNotification;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.QMaintenanceNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class MaintenanceNotificationDao extends AbstractNotificationDao<MaintenanceNotification> {

    public List<MaintenanceNotification> findAllInDateRange(Date fromDate, Date tillDate) {
        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;
        return queryFactory.selectFrom(maintenanceNotification)
                .where(maintenanceNotification.date.between(fromDate, tillDate))
                .fetch();
    }

    public boolean doesDateRangeContainMoreThanNumberWithinProperty(Date fromDate, Date tillDate, long number, Property property) {
        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;

        return queryFactory.selectFrom(maintenanceNotification)
                .where(maintenanceNotification.property.eq(property))
                .where(maintenanceNotification.date.between(fromDate, tillDate))
                .fetchCount() < number;
    }
}
