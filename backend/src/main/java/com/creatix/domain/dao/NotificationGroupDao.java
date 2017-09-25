package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.NotificationGroup;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Repository
@Transactional
public class NotificationGroupDao extends DaoBase<NotificationGroup, Long> {


    @Override
    public void persist(NotificationGroup entity) {
        if ( entity.getCreatedAt() == null ) {
            entity.setCreatedAt(OffsetDateTime.now());
        }

        super.persist(entity);
    }
}
