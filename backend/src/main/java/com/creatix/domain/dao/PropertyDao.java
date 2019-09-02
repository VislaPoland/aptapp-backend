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
    
    public List<Property> findAll(Integer page, Integer size, String keywords) {
        return queryFactory.selectFrom(QProperty.property)
                .where(
                		(QProperty.property.deleteDate.isNull()).and(
                				(QProperty.property.name.toLowerCase().contains(keywords)).or
                				(QProperty.property.address.houseNumber.toLowerCase().contains(keywords)).or
                				(QProperty.property.address.streetName.toLowerCase().contains(keywords)).or
                				(QProperty.property.address.town.toLowerCase().contains(keywords)).or
                				(QProperty.property.address.state.toLowerCase().contains(keywords)).or
                				(QProperty.property.address.zipCode.toLowerCase().contains(keywords)).or
                				(QProperty.property.name.toLowerCase().contains(keywords))
                	   )
                )
                .orderBy(QProperty.property.name.lower().asc())
                .limit(size).offset(page*size)
                .fetch();
    }

}
