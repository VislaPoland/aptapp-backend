package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.entity.store.account.Tenant;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SubTenantDao extends AbstractAccountDao<SubTenant> {
}
