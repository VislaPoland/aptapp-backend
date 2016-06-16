package com.creatix.domain.dao;

import com.creatix.domain.entity.Apartment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ApartmentDao extends DaoBase<Apartment, String> {
}
