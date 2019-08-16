package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.enums.EmailTemplateName;

public class TenantActivationMessageTemplate extends ActivationMessageTemplate {

    public TenantActivationMessageTemplate(Tenant account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getSubject() {
        return "Welcome to Apt. App";
    }

    public String getPropertyName() {
        return account.getProperty().getName();
    }

    @Override
    public String getTemplateName() {
        return EmailTemplateName.ACTIVATION_TENANT.getValue();
    }
}
