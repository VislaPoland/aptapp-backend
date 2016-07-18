package com.creatix.message.template;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.PropertyOwner;

public class PropertyOwnerActivationMessageTemplate extends ActivationMessageTemplate {

    public PropertyOwnerActivationMessageTemplate(PropertyOwner account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getTemplateName() {
        return "activation-property-owner";
    }
}
