package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.entity.store.account.Tenant;
import lombok.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.creatix.domain.entity.store.account.QTenant.tenant;

@Repository
@Transactional
public class TenantDao extends AbstractAccountDao<Tenant> {

    @PersistenceContext
    protected EntityManager em;

    public List<Tenant> listTenantsForApartment(Apartment apartment) {
        return queryFactory.selectFrom(tenant)
                .where(tenant.apartment.eq(apartment))
                .fetch();
    }

    public List<Tenant> findByProperty(Long propertyId){
        return queryFactory.selectFrom(tenant)
                .where(tenant.apartment.property.id.eq(propertyId))
                .fetch();
    }

    @Override
    public String generateSQLUpdateParams(Tenant account) {
        return super.generateSQLUpdateParams(account) +
                ", apartment_id=" + account.getApartment().getId() +
                ", enable_sms=" + account.getEnableSms() +
                ", is_privacy_policy_accepted=" + account.getIsPrivacyPolicyAccepted() +
                ", is_tac_accepted=" + account.getIsTacAccepted() +
                ", parent_tenant_id=null";
    }

    public void persistSubTenantToTenant(@NonNull Tenant tenant, @NonNull Long subtenantId) {
        em.createNativeQuery("UPDATE account SET " + generateSQLUpdateParams(tenant) + " WHERE id = " + tenant.getId()).executeUpdate();
        em.createNativeQuery("UPDATE parking_stall SET using_tenant_id = " + tenant.getId() + " WHERE using_tenant_id = " + subtenantId).executeUpdate();
        em.createNativeQuery("UPDATE vehicle SET owner_id = " + tenant.getId() + " WHERE owner_id = " + subtenantId).executeUpdate();
    }

}
