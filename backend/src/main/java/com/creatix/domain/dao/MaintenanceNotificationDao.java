package com.creatix.domain.dao;

import com.creatix.domain.entity.MaintenanceNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MaintenanceNotificationDao extends AbstractNotificationDao<MaintenanceNotification> {
}
