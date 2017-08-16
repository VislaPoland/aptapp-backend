package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.PersonalMessageGroup;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Repository
@Transactional
public class PersonalMessageGroupDao extends DaoBase<PersonalMessageGroup, Long> {


    @Override
    public void persist(PersonalMessageGroup entity) {
        if ( entity.getCreatedAt() == null ) {
            entity.setCreatedAt(OffsetDateTime.now());
        }

        super.persist(entity);
    }
}
