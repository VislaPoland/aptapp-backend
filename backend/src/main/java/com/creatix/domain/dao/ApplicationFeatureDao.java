package com.creatix.domain.dao;

import com.creatix.domain.entity.store.ApplicationFeature;
import com.creatix.domain.entity.store.Property;
import static com.creatix.domain.entity.store.QApplicationFeature.applicationFeature;

import com.creatix.domain.entity.store.QApplicationFeature;
import com.creatix.domain.enums.ApplicationFeatureType;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Tomas Michalek on 11/04/2017.
 */
@Repository
public class ApplicationFeatureDao extends DaoBase<ApplicationFeature, Long> {


    /**
     * Finds application feature configuration by type and property.
     * By default features are enabled, if configuration is not found, configuration with enabled feature is returned
     *
     * @param featureType
     * @param property
     * @return Application feature configuration. Or dummy one if not found, with feature enabled
     */
    @NotNull
    public ApplicationFeature findByFeatureTypeAndApartment(@NotNull ApplicationFeatureType featureType, Property property) {
        ApplicationFeature feature = queryFactory
                .selectFrom(applicationFeature)
                .where(
                        applicationFeature.property.eq(property)
                                .and(
                                        applicationFeature.applicationFeatureType.eq(featureType)
                                )
                ).fetchOne();
        return  feature == null ?
                    new ApplicationFeature().setEnabled(true).setApplicationFeatureType(featureType).setProperty(property)
                    : feature;
    }


    public List<ApplicationFeature> listByProperty(Property property) {
        return queryFactory.selectFrom(applicationFeature).where(
                applicationFeature.property.eq(property)
        ).fetch();
    }
}
