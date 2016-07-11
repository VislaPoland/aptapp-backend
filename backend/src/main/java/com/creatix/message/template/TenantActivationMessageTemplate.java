package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;

import java.net.URL;

public class TenantActivationMessageTemplate extends ActivationMessageTemplate {

    public TenantActivationMessageTemplate(Account account, URL baseUrl) {
        super(account, baseUrl);
    }

    @Override
    public String getTemplateName() {
        return "activation-tenant";
    }
}
