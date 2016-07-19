package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.Tenant;

public class TenantActivationMessageTemplate extends ActivationMessageTemplate {

    public TenantActivationMessageTemplate(Tenant account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getTemplateName() {
        return "activation-tenant";
    }
}
