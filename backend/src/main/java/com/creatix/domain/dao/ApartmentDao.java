package com.creatix.domain.dao;

import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Property;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ApartmentDao extends DaoBase<Apartment, Long> {
    public Apartment findByUnitNumberWithinProperty(Property property, String unitNumber) {
        return em.createQuery("SELECT a FROM Apartment a WHERE a.property = :property AND a.unitNumber = :unitNumber", Apartment.class)
                .setParameter("property", property)
                .setParameter("unitNumber", unitNumber)
                .getSingleResult();
    }
}
