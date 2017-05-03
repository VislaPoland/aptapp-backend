package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.account.Tenant;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.creatix.domain.entity.store.account.QTenant.tenant;

@Repository
@Transactional
public class TenantDao extends AbstractAccountDao<Tenant> {

    public List<Tenant> listTenantsForApartment(Apartment apartment) {
        return queryFactory.selectFrom(tenant)
                .where(tenant.apartment.eq(apartment))
                .fetch();
    }

}
