package com.creatix.domain.dao;

import com.creatix.domain.entity.account.PropertyManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PropertyManagerDao extends DaoBase<PropertyManager, Long> {
}
