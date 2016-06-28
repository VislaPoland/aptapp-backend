package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.PropertyOwnerDao;
import com.creatix.domain.dto.property.CreatePropertyRequest;
import com.creatix.domain.dto.property.UpdatePropertyRequest;
import com.creatix.domain.entity.Account;
import com.creatix.domain.entity.Employee;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.PropertyOwner;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.PropertyStatus;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PropertyOwnerDao propertyOwnerDao;
    @Autowired
    private Mapper mapper;
    @Autowired
    private AuthorizationManager authorizationManager;

    private boolean isEligibleToReadProperty(Property property, Account account) {
        switch (account.getRole()) {
            case Administrator:
                return true;
            case PropertyOwner:
                return property.getOwner().equals(account);
            case PropertyManager:
                //noinspection SuspiciousMethodCalls
                return property.getManagers().contains(account);
            case AssistantPropertyManager:
                return property.getManagers().contains(((Employee) account).getManager());
            default:
                return false;
        }
    }

    // same as read accessibility except Administrator
    private boolean isEligibleToUpdateProperty(Property property, Account account) {
        return !account.getRole().equals(AccountRole.Administrator) && isEligibleToReadProperty(property, account);
    }

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if (item == null) {
            throw ex;
        }
        return item;
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public List<Property> getAllProperties() {
        return propertyDao.findAll().stream()
                .filter(p -> isEligibleToReadProperty(p, authorizationManager.getCurrentAccount()))
                .collect(Collectors.toList());
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public Property getProperty(Long propertyId) {
        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property %d not found", propertyId)));
        if (isEligibleToReadProperty(property, authorizationManager.getCurrentAccount())) {
            return property;
        }
        throw new SecurityException(String.format("You are not eligible to read info about property with id=%d", propertyId));
    }

    @RoleSecured(AccountRole.Administrator)
    public Property createFromRequest(@NotNull CreatePropertyRequest request) {
        Objects.requireNonNull(request);

        final Property property = mapper.toProperty(request);
        final PropertyOwner propertyOwner = getOrElseThrow(request.getPropertyOwnerId(), propertyOwnerDao,
                new EntityNotFoundException(String.format("Property owner %d not found", request.getPropertyOwnerId())));
        property.setOwner(propertyOwner);
        property.setStatus(PropertyStatus.Draft);
        propertyDao.persist(property);
        return property;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public Property updateFromRequest(Long propertyId, @NotNull UpdatePropertyRequest request) {
        Objects.requireNonNull(request);

        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property %d not found", propertyId)));

        if (isEligibleToUpdateProperty(property, authorizationManager.getCurrentAccount())) {
            if (request.getPropertyOwnerId() != null) {
                final PropertyOwner propertyOwner = propertyOwnerDao.findById(request.getPropertyOwnerId());
                if (propertyOwner == null) {
                    throw new EntityNotFoundException(String.format("Property owner %d not found", request.getPropertyOwnerId()));
                }
                property.setOwner(propertyOwner);
            }
            mapper.fillProperty(request, property);
            propertyDao.persist(property);
            return property;
        }

        throw new SecurityException(String.format("You are not eligible to update info about property with id=%d", propertyId));
    }

    @RoleSecured(AccountRole.Administrator)
    public Property deleteProperty(long propertyId) {
        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property %d not found", propertyId)));
        property.setDeleteDate(new Date());
        propertyDao.persist(property);
        return property;
    }
}
