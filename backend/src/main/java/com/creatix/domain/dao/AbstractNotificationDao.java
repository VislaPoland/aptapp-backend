package com.creatix.domain.dao;

import com.creatix.domain.entity.Notification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public abstract class AbstractNotificationDao<T extends Notification> extends DaoBase<T, Long> {
}
