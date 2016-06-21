package com.creatix.domain.dao;

import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Property;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Repository
@Transactional
public class ApartmentDao extends DaoBase<Apartment, Long> {
    public Apartment findByUnitNumberWithinProperty(String unitNumber, Property property) {
        try {
            return em.createQuery("SELECT a FROM Apartment a WHERE a.property = :property AND a.unitNumber = :unitNumber", Apartment.class)
                    .setParameter("property", property)
                    .setParameter("unitNumber", unitNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
