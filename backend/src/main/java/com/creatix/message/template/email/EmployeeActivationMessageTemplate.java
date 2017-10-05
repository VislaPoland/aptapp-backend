package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.EmployeeBase;

public class EmployeeActivationMessageTemplate extends ActivationMessageTemplate {

    public EmployeeActivationMessageTemplate(EmployeeBase account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getSubject() {
        return "Welcome to Apt. App!";
    }

    @Override
    public String getTemplateName() {
        return "activation-employee";
    }
}
