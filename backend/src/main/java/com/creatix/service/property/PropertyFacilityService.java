package com.creatix.service.property;

import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.PropertyFacilityDao;
import com.creatix.domain.dto.property.facility.CreatePropertyFacilityRequest;
import com.creatix.domain.dto.property.facility.UpdatePropertyFacilityRequest;
import com.creatix.domain.entity.store.Facility;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.PropertyMapper;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyFacilityService {
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PropertyFacilityDao propertyFacilityDao;
    @Autowired
    private PropertyMapper propertyMapper;
    @Autowired
    private AuthorizationManager authorizationManager;

    @RoleSecured
    public List<Facility> details(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId);

        authorizationManager.checkAccess(this.getProperty(propertyId));

        return propertyFacilityDao
                .findAllByProperty(propertyId).stream()
                .collect(Collectors.toList());
    }

    @RoleSecured
    public Facility detail(@NotNull Long propertyId, @NotNull Long facilityId) {
        Objects.requireNonNull(propertyId);

        authorizationManager.checkAccess(this.getProperty(propertyId));

        final Facility facilty = this.getFacilty(propertyId, facilityId);

        return facilty;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public Facility create(@NotNull Long propertyId, @NotNull CreatePropertyFacilityRequest request) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(request);

        final Property property = this.getProperty(propertyId);
        authorizationManager.checkAccess(property);

        final Facility facility = propertyMapper.toPropertyFacility(request);
        facility.setProperty(property);
        propertyFacilityDao.persist(facility);

        return facility;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public Facility update(@NotNull Long propertyId, @NotNull Long facilityId, @NotNull UpdatePropertyFacilityRequest request) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(request);

        final Property property = this.getProperty(propertyId);
        authorizationManager.checkAccess(property);

        final Facility facility = this.getFacilty(propertyId, facilityId);
        propertyMapper.fillPropertyFacility(request, facility);
        propertyFacilityDao.persist(facility);

        return facility;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public Facility delete(@NotNull Long propertyId, @NotNull Long facilityId) {
        Objects.requireNonNull(propertyId);

        final Property property = this.getProperty(propertyId);
        authorizationManager.checkAccess(property);

        final Facility facility = this.getFacilty(propertyId, facilityId);
        propertyFacilityDao.delete(facility);

        return facility;
    }

    private Property getProperty(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId);
        final Property property = this.propertyDao.findById(propertyId);
        if (property == null) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }
        return property;
    }

    private Facility getFacilty(@NotNull Long propertyId, @NotNull Long facilityId) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(facilityId);
        final Facility facility = this.propertyFacilityDao.findById(propertyId, facilityId);
        if (facility == null) {
            throw new EntityNotFoundException(String.format("Facility id=%d not found", facilityId));
        }
        return facility;
    }
}
