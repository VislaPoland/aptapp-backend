package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.Tenant;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TenantDao extends AbstractAccountDao<Tenant> {
}
