package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.EmployeeBase;
import com.creatix.domain.enums.EmailTemplateName;

public class EmployeeActivationMessageTemplate extends ActivationMessageTemplate {

    private final EmployeeBase account;
    private final Property property;

    public EmployeeActivationMessageTemplate(EmployeeBase account, Property property, ApplicationProperties properties) {
        super(account, properties);
        this.account = account;
        this.property = property;
    }

    @Override
    public String getSubject() {
        return "Welcome to Apt. App!";
    }

    public String getPropertyName() {
        return property.getName();
    }

    @Override
    public String getTemplateName() {
        return EmailTemplateName.ACTIVATION_EMPLOYEE.getValue();
    }
}
