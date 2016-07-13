package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;

import java.net.URL;

public class PropertyOwnerActivationMessageTemplate extends ActivationMessageTemplate {

    public PropertyOwnerActivationMessageTemplate(Account account, URL backendUrl, URL frontendUrl) {
        super(account, backendUrl, frontendUrl);
    }

    @Override
    public String getTemplateName() {
        return "activation-property-owner";
    }
}
