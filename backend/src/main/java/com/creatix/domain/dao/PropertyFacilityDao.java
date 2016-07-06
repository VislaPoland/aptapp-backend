package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Facility;
import com.creatix.domain.entity.store.QFacility;
import com.creatix.domain.entity.store.QProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PropertyFacilityDao extends DaoBase<Facility, Long> {

    public List<Facility> findAllByProperty(Long propertyId) {
        return this.queryFactory
                .selectFrom(QFacility.facility)
                .where(QProperty.property.id.eq(propertyId))
                .fetch();
    }

    public Facility findById(Long propertyId, Long facilityId) {
        return this.queryFactory
                .selectFrom(QFacility.facility)
                .where(QFacility.facility.id.eq(facilityId))
                .where(QProperty.property.id.eq(propertyId))
                .fetchFirst();
    }

}
