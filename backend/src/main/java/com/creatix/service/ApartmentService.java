package com.creatix.service;

import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Property;
import com.creatix.security.AuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class ApartmentService {

    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private AuthorizationManager authorizationManager;

    public Apartment getApartment(Long apartmentId) {
        Apartment apartment = apartmentDao.findById(apartmentId);
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment with id %d not found", apartmentId));
        }

        return apartment;
    }

    public List<Apartment> getApartmentsByPropertyId(long propertyId) {
        final Property property = propertyDao.findById(propertyId);
        authorizationManager.checkManager(property);

        return apartmentDao.findByProperty(property);
    }
}
