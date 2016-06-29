package com.creatix.service.property;

import com.creatix.domain.dao.PropertyContactDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dto.property.contact.CreatePropertyContactRequest;
import com.creatix.domain.dto.property.contact.UpdatePropertyContactRequest;
import com.creatix.domain.entity.Contact;
import com.creatix.domain.entity.Property;
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
public class PropertyContactService {
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PropertyContactDao propertyContactDao;
    @Autowired
    private PropertyMapper propertyMapper;
    @Autowired
    private AuthorizationManager authorizationManager;

    @RoleSecured
    public List<Contact> details(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId);

        authorizationManager.checkAccess(this.getProperty(propertyId));

        return propertyContactDao
                .findAllByProperty(propertyId).stream()
                .collect(Collectors.toList());
    }

    @RoleSecured
    public Contact detail(@NotNull Long propertyId, @NotNull Long contactId) {
        Objects.requireNonNull(propertyId);

        authorizationManager.checkAccess(this.getProperty(propertyId));

        final Contact contact = this.getContact(propertyId, contactId);

        return contact;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public Contact create(@NotNull Long propertyId, @NotNull CreatePropertyContactRequest request) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(request);

        final Property property = this.getProperty(propertyId);
        authorizationManager.checkAccess(property);

        final Contact contact = propertyMapper.toPropertyContact(request);
        property.getContacts().add(contact);
        propertyDao.persist(property);

        return contact;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public Contact update(@NotNull Long propertyId, @NotNull Long contactId, @NotNull UpdatePropertyContactRequest request) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(request);

        final Property property = this.getProperty(propertyId);
        authorizationManager.checkAccess(property);

        final Contact contact = this.getContact(propertyId, contactId);
        propertyMapper.fillPropertyContact(request, contact);
        propertyContactDao.persist(contact);

        return contact;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public Contact delete(@NotNull Long propertyId, @NotNull Long contactId) {
        Objects.requireNonNull(propertyId);

        final Property property = this.getProperty(propertyId);
        authorizationManager.checkAccess(property);

        final Contact contact = this.getContact(propertyId, contactId);
        property.getContacts().remove(contact);
        propertyDao.persist(property);

        return contact;
    }

    private Property getProperty(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId);
        final Property property = this.propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }
        return property;
    }

    private Contact getContact(@NotNull Long propertyId, @NotNull Long contactId) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(contactId);
        final Contact contact = this.propertyContactDao.findById(propertyId, contactId);
        if ( contact == null ) {
            throw new EntityNotFoundException(String.format("Contact id=%d not found", contactId));
        }
        return contact;
    }
}
