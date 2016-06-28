package com.creatix.domain.dao;

import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.QApartment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ApartmentDao extends DaoBase<Apartment, Long> {

    public Apartment findByUnitNumberWithinProperty(String unitNumber, Property property) {
        final QApartment apartment = QApartment.apartment;
        return queryFactory.selectFrom(apartment)
                .where(apartment.property.eq(property).and(apartment.unitNumber.eq(unitNumber)))
                .fetchOne();

    }

    public List<Apartment> findByProperty(Property property) {
        final QApartment apartment = QApartment.apartment;
        return queryFactory.selectFrom(apartment)
                .where(apartment.property.eq(property))
                .fetch();
    }
}
