package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;

import java.net.URL;

public class EmployeeActivationMessageTemplate extends ActivationMessageTemplate {

    public EmployeeActivationMessageTemplate(Account account, URL baseUrl) {
        super(account, baseUrl);
    }

    @Override
    public String getTemplateName() {
        return "activation-employee";
    }
}
