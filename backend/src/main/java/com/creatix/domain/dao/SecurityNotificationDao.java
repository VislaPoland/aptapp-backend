package com.creatix.domain.dao;

import com.creatix.domain.entity.store.SecurityNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SecurityNotificationDao extends AbstractNotificationDao<SecurityNotification> {
}
