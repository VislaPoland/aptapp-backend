package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.EscalatedNeighborhoodNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EscalatedNeighborhoodNotificationDao extends AbstractNotificationDao<EscalatedNeighborhoodNotification> {

}
