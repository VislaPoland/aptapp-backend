package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.SubTenant;

public class SubTenantActivationMessageTemplate extends ActivationMessageTemplate {

    public SubTenantActivationMessageTemplate(SubTenant account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getTemplateName() { return "activation-tenant"; }
}
