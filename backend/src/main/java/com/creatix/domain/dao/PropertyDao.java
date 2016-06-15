package com.creatix.domain.dao;

import com.creatix.domain.entity.Property;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PropertyDao extends DaoBase<Property, Long> {
}
