package com.creatix.domain.dao;

import com.creatix.domain.entity.account.SubTenant;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SubTenantDao extends DaoBase<SubTenant, Long> {
}
