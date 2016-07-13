package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;

import java.net.URL;

public class EmployeeActivationMessageTemplate extends ActivationMessageTemplate {

    public EmployeeActivationMessageTemplate(Account account, URL backendUrl, URL frontendUrl) {
        super(account, backendUrl, frontendUrl);
    }

    @Override
    public String getTemplateName() {
        return "activation-employee";
    }
}
