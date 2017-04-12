package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.ApplicationFeature;
import com.creatix.domain.enums.ApplicationFeatureType;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

/**
 * Created by kvimbi on 11/04/2017.
 */
@Repository
public class ApplicationFeatureDao extends DaoBase<ApplicationFeature, Long> {


    /**
     * Todo: enabled by default
     * @param featureType
     * @param apartmentId
     * @return
     */
    public ApplicationFeature findByFeatureTypeAndApartment(@NotNull ApplicationFeatureType featureType, Long apartmentId) {
        Apartment apartment = new Apartment();
        apartment.setId(apartmentId);
        return new ApplicationFeature()
                .setApplicationFeatureType(featureType)
                .setApartment(apartment)
                .setEnabled(true);
    }
}
