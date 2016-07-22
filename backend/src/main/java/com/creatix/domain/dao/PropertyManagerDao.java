package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.PropertyManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.creatix.domain.entity.store.account.QPropertyManager.propertyManager;

@Repository
@Transactional
public class PropertyManagerDao extends DaoBase<PropertyManager, Long> {
    public List<PropertyManager> findByProperty(Property property) {
        return queryFactory
                .selectFrom(propertyManager)
                .where(propertyManager.deletedAt.isNull()
                        .and(propertyManager.managedProperty.eq(property)))
                .fetch();
    }
}
