package com.creatix.domain.dao;

import com.creatix.domain.entity.Tenant;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TenantDao extends DaoBase<Tenant, Long> {
}
