package com.creatix.domain.dao;

import com.creatix.domain.entity.Facility;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.QFacility;
import com.creatix.domain.entity.QProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
