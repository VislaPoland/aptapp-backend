package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;

public class ActivationMessageTemplate implements EmailMessageTemplate {

    private final Account account;

    public ActivationMessageTemplate(Account account) {
        this.account = account;
    }

    @Override
    public String getTemplateName() {
        return "activation";
    }

    @Override
    public String getSubject() {
        return "Welcome to Apt. – A smarter way to communicate.";
    }

    @Override
    public String getRecipient() {
        return account.getPrimaryEmail();
    }

    public String getName() {
        return account.getFullName();
    }

    public String getActivationToken() {
        return account.getActionToken();
    }

    public String getLinkToPage() {
        return "https://www.aptapp.com";
    }
}
