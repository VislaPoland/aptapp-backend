package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.QProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PropertyDao extends DaoBase<Property, Long> {

    public List<Property> findAll() {
        return queryFactory.selectFrom(QProperty.property)
                .where(QProperty.property.deleteDate.isNull())
                .orderBy(QProperty.property.name.lower().asc())
                .fetch();
    }

}
