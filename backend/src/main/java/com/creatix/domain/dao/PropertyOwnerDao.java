package com.creatix.domain.dao;

import com.creatix.domain.entity.account.PropertyOwner;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PropertyOwnerDao extends DaoBase<PropertyOwner, Long> {
}
