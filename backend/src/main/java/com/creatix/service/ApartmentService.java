package com.creatix.service;

import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class ApartmentService {
    @Autowired
    private ApartmentDao apartmentDao;

    public Apartment getApartment(Long apartmentId) {
        Apartment apartment = apartmentDao.findById(apartmentId);
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment with id %d not found", apartmentId));
        }

        return apartment;
    }
}
