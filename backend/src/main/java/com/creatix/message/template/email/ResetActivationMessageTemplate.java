package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.Account;

public class ResetActivationMessageTemplate extends ActivationMessageTemplate {

    public ResetActivationMessageTemplate(Account account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getSubject() {
        return "Activation code for Apt. App – A smarter way to communicate";
    }

    @Override
    public String getTemplateName() {
        return "activation-reset";
    }
}
