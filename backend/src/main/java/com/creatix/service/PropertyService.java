package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.PropertyOwnerDao;
import com.creatix.domain.dto.property.CreatePropertyRequest;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.PropertyOwner;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.PropertyStatus;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PropertyOwnerDao propertyOwnerDao;
    @Autowired
    private Mapper mapper;

    @RoleSecured
    public Property getProperty(long propertyId) {
        final Property property = propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property %d not found", propertyId));
        }
        return property;
    }

    @RoleSecured(AccountRole.Administrator)
    public Property createFromRequest(@NotNull CreatePropertyRequest request) {
        Objects.requireNonNull(request);

        final Property property = mapper.toProperty(request);
        final PropertyOwner propertyOwner = propertyOwnerDao.findById(request.getPropertyOwnerId());
        if ( propertyOwner == null ) {
            throw new EntityNotFoundException(String.format("Property owner %d not found", request.getPropertyOwnerId()));
        }
        property.setOwner(propertyOwner);
        property.setStatus(PropertyStatus.Draft);
        propertyDao.persist(property);
        return property;
    }
}
