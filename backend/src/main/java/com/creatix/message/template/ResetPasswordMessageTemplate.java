package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;

import java.net.MalformedURLException;
import java.net.URL;

public class ResetPasswordMessageTemplate implements EmailMessageTemplate {

    private final Account account;
    private final URL baseUrl;

    public ResetPasswordMessageTemplate(Account account, URL baseUrl) {
        this.account = account;
        this.baseUrl = baseUrl;
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

    public String getApplicationUrl() {
        return baseUrl.toString();
    }

    public String getLogoUrl() throws MalformedURLException {
        return new URL(baseUrl, "/static/images/aptapp_logo.png").toString();
    }

    public String getIconUrl() throws MalformedURLException {
        return new URL(baseUrl, "/static/images/aptapp_icon.png").toString();
    }
}

