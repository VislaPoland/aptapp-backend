package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.NotificationHistory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class NotificationHistoryDao extends DaoBase<NotificationHistory, Long> {
}
