package com.creatix.message.template;

import com.creatix.domain.entity.account.Account;

public class ResetPasswordMessageTemplate implements EmailMessageTemplate {

    private final Account account;

    public ResetPasswordMessageTemplate(Account account) {
        this.account = account;
    }

    @Override
    public String getTemplateName() {
        return "reset-password";
    }

    @Override
    public String getSubject() {
        return "Apt. Password Reset Request";
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

