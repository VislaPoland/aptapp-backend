package com.creatix.domain.dao;

import com.creatix.domain.entity.Notification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
abstract class AbstractNotificationDao<T extends Notification> extends DaoBase<T, Long> {
}
