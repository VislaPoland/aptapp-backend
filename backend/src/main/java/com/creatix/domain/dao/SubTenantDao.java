package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.entity.store.account.Tenant;
import lombok.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SubTenantDao extends AbstractAccountDao<SubTenant> {

    @Override
    public String generateSQLUpdateParams(SubTenant account) {
        return super.generateSQLUpdateParams(account) +
                ", parent_tenant_id=" + account.getParentTenant().getId() +
                ", is_privacy_policy_accepted=" + account.getIsPrivacyPolicyAccepted() +
                ", is_tac_accepted=" + account.getIsTacAccepted() +
                ", apartment_id=null" +
                ", enable_sms=null";

    }

    public void persistTenantToSubTenant(@NonNull SubTenant subTenant) {
        em.createNativeQuery("UPDATE Account SET " + generateSQLUpdateParams(subTenant) + " WHERE id = " + subTenant.getId()).executeUpdate();
    }
}
