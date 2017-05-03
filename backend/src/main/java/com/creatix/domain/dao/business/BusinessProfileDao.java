package com.creatix.domain.dao.business;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.business.BusinessCategory;
import com.creatix.domain.entity.store.business.BusinessProfile;
import static com.creatix.domain.entity.store.business.QBusinessProfile.businessProfile;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Repository
@Transactional
public class BusinessProfileDao extends DaoBase<BusinessProfile, Long> {

    public List<BusinessProfile> listBusinessesForProperty(Property property) {
        return queryFactory
                .selectFrom(businessProfile)
                .where(
                        businessProfile.property.eq(property)
                )
                .fetch();
    }

    public List<BusinessProfile> listBusinessesForPropertyAndCategory(Property property, BusinessCategory cateogory) {
        return queryFactory
                .selectFrom(businessProfile)
                .where(
                        businessProfile.property.eq(property).and(
                                businessProfile.businessCategoryList.contains(cateogory)
                        )
                )
                .fetch();
    }

    public List<BusinessProfile> searchBusinesses(Property property, String name, BusinessCategory businessCategory) {
        BooleanExpression filter = businessProfile.property.eq(property);
        filter.and(businessProfile.name.like(name));
        if (null != businessCategory) {
            filter.and(businessProfile.businessCategoryList.contains(businessCategory));
        }
        return queryFactory
                .selectFrom(businessProfile)
                .where(filter)
                .fetch();
    }




}
