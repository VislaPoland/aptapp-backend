package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.PropertyOwner;
import com.creatix.domain.enums.EmailTemplateName;

public class PropertyOwnerActivationMessageTemplate extends ActivationMessageTemplate {

    public PropertyOwnerActivationMessageTemplate(PropertyOwner account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getTemplateName() {
        return EmailTemplateName.ACTIVATION_PROPERTY_OWNER.getValue();
    }
}
