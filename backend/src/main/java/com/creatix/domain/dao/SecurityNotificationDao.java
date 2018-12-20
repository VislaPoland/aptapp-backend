package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.SecurityNotification;
import com.creatix.security.AuthorizationManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SecurityNotificationDao extends AbstractNotificationDao<SecurityNotification> {

    public SecurityNotificationDao(AuthorizationManager authorizationManager, NotificationGroupDao notificationGroupDao, NotificationHistoryDao notificationHistoryDao) {
        super(authorizationManager, notificationGroupDao, notificationHistoryDao);
    }

    @Override
    public void persist(SecurityNotification entity) {
        super.persist(entity);
        createNotificationHistoryLog(entity, entity.getStatus().name());
    }

}
