package com.creatix.domain.dao;

import com.creatix.domain.entity.store.NeighborhoodNotification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class NeighborhoodNotificationDao extends AbstractNotificationDao<NeighborhoodNotification> {
}
