package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.entity.store.notification.QMaintenanceNotification;
import com.creatix.security.AuthorizationManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
@Transactional
public class MaintenanceNotificationDao extends AbstractNotificationDao<MaintenanceNotification> {
    private final AuthorizationManager authorizationManager;

    @Autowired
    public MaintenanceNotificationDao(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }


    public List<MaintenanceNotification> findAllInDateRange(OffsetDateTime fromDate, OffsetDateTime tillDate) {
        final QMaintenanceNotification maintenanceNotification = QMaintenanceNotification.maintenanceNotification;
        return queryFactory.selectFrom(maintenanceNotification)
                .where(maintenanceNotification.createdAt.between(fromDate, tillDate))
                .fetch();
    }

    @Override
    public void persist(MaintenanceNotification entity) {
        entity.setUpdatedByAccount(authorizationManager.getCurrentAccount());

        super.persist(entity);
    }

}
