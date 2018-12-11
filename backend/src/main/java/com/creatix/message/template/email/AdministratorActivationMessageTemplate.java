package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.EmailTemplateName;

public class AdministratorActivationMessageTemplate extends ActivationMessageTemplate {

    public AdministratorActivationMessageTemplate(Account account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getTemplateName() {
        return EmailTemplateName.ACTIVATION_ADMINISTRATOR.getValue();
    }
}
