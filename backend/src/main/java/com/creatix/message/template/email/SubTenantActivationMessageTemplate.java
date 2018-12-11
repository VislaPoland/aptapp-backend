package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.enums.EmailTemplateName;

public class SubTenantActivationMessageTemplate extends ActivationMessageTemplate {

    public SubTenantActivationMessageTemplate(SubTenant account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getTemplateName() { return EmailTemplateName.ACTIVATION_TENANT.getValue(); }
}
