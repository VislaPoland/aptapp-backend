package com.creatix.domain.dao;

import com.creatix.domain.entity.store.PropertyLogo;
import com.creatix.domain.entity.store.QPropertyLogo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PropertyLogoDao extends DaoBase<PropertyLogo, Long> {


    public PropertyLogo findByPropertyId(Long propertyId) {
        final QPropertyLogo logo = QPropertyLogo.propertyLogo;
        return queryFactory.selectFrom(logo)
                .where(logo.property.id.eq(propertyId))
                .fetchOne();
    }
}
