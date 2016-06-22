package com.creatix.domain.dao;

import com.creatix.domain.entity.SecurityNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SecurityNotificationDao extends AbstractNotificationDao<SecurityNotification> {
}
