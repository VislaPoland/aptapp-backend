package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.Tenant;

import java.net.URL;

public class TenantActivationMessageTemplate extends ActivationMessageTemplate {

    public TenantActivationMessageTemplate(Tenant account, URL backendUrl, URL frontendUrl) {
        super(account, backendUrl, frontendUrl);
    }

    @Override
    public String getTemplateName() {
        return "activation-tenant";
    }
}
