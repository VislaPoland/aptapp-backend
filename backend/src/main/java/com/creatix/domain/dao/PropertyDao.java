package com.creatix.domain.dao;

import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.QProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PropertyDao extends DaoBase<Property, Long> {

    public List<Property> findAll() {
        return queryFactory.selectFrom(QProperty.property)
                .where(QProperty.property.deleteDate.isNull())
                .fetch();

    }

}
