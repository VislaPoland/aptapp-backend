package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Contact;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.QProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PropertyContactDao extends DaoBase<Contact, Long> {

    public List<Contact> findAllByProperty(Long propertyId) {
        Property property = queryFactory
                .selectFrom(QProperty.property)
                .where(QProperty.property.id.eq(propertyId))
                .fetchOne();
        if (property != null) {
            return new ArrayList<>(property.getContacts());
        }
        return null;
    }

    public Contact findById(Long propertyId, Long contactId) {
        //  TODO: update query to effective selecting
        List<Contact> contacts = this.findAllByProperty(propertyId);
        if (contacts != null) {
            Optional<Contact> filtered = contacts.stream().filter(c -> c.getId().equals(contactId)).findFirst();
            return filtered.isPresent() ? filtered.get() : null;
        }
        return null;
    }

}
