package com.creatix.service;

import com.creatix.domain.dao.ApplicationFeatureDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.entity.store.ApplicationFeature;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.enums.ApplicationFeatureType;
import com.creatix.security.AuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Component
@Transactional
public class ApplicationFeatureService {


    @Autowired
    private ApplicationFeatureDao applicationFeatureDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private AuthorizationManager authorizationManager;

    private List<ApplicationFeature> removeDeprecatedFeatures(final List<ApplicationFeature> features) {
        final List<ApplicationFeature> retList = new LinkedList<>(features);
        features.forEach(applicationFeature -> {
            Optional<ApplicationFeatureType> found = Arrays.stream(ApplicationFeatureType.values())
                    .filter(e -> e == applicationFeature.getApplicationFeatureType())
                    .findFirst();
            if (!found.isPresent()) {
                applicationFeatureDao.delete(applicationFeature);
                retList.remove(applicationFeature);
            }
        });

        return retList;
    }

    private List<ApplicationFeature> createMissing(final List<ApplicationFeature> features, final Property property) {
        final List<ApplicationFeature> retList = new LinkedList<>(features);
        Arrays.stream(ApplicationFeatureType.values()).forEach(featureType -> {
            Optional<ApplicationFeature> found = features.stream()
                    .filter(applicationFeature -> applicationFeature.getApplicationFeatureType() == featureType)
                    .findFirst();

            if (!found.isPresent()) {
                ApplicationFeature entity = new ApplicationFeature()
                        .setApplicationFeatureType(featureType)
                        .setEnabled(true)
                        .setProperty(property);
                applicationFeatureDao.persist(
                        entity
                );
                retList.add(entity);
            }
        });

        return retList;
    }

    @NotNull
    public List<ApplicationFeature> listFeaturesByProperty(long propertyId) {
        Property property = propertyDao.findById(propertyId);
        if (null == property) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }
        final List<ApplicationFeature> features = applicationFeatureDao.listByProperty(property);
        if (features.size() != ApplicationFeatureType.values().length) {
            return createMissing(removeDeprecatedFeatures(features), property);
        } else {
            return features;
        }
    }

    public ApplicationFeature updateFeatureStatus(@NotNull Long applicationFeatureId, @NotNull boolean status) {
        ApplicationFeature applicationFeature = applicationFeatureDao.findById(applicationFeatureId);

        if (null == applicationFeature) {
            throw new EntityNotFoundException(String.format("Feature id=%d not found", applicationFeatureId));
        }

        if (authorizationManager.canWrite(applicationFeature.getProperty())) {
            applicationFeature.setEnabled(status);
            applicationFeatureDao.persist(applicationFeature);
            return applicationFeature;
        }

        throw new SecurityException(String.format("You are not eligible to update info about property with id=%d", applicationFeature.getProperty().getId()));
    }

}
