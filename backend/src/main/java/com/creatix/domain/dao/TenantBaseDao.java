package com.creatix.domain.dao;

import com.creatix.domain.entity.account.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TenantBaseDao extends AbstractAccountDao<TenantBase> {

    public TenantBase findById(Long apartmentId, Long tenantId) {
        TenantBase tenantBase = null;

        final QTenant tenant = QTenant.tenant;
        tenantBase = queryFactory
                .selectFrom(tenant)
                .where(tenant.apartment.id.eq(apartmentId).
                        and(tenant.id.eq(tenantId)))
                .fetchFirst();

        if (tenantBase != null) {
            return tenantBase;
        }

        final QSubTenant subTenant = QSubTenant.subTenant;
        tenantBase = queryFactory
                .selectFrom(subTenant)
                .where(subTenant.parentTenant.apartment.id.eq(apartmentId).
                        and(subTenant.id.eq(tenantId)))
                .fetchFirst();

        return tenantBase;
    }

}
