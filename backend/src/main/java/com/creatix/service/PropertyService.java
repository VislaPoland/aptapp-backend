package com.creatix.service;

import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.entity.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class PropertyService {
    @Autowired
    private PropertyDao propertyDao;

    public Property getProperty(Long id) {
        Property property = propertyDao.findById(id);
        if (property == null) {
            throw new EntityNotFoundException(String.format("Property with id %d not found", id));
        }
        return property;
    }
}
