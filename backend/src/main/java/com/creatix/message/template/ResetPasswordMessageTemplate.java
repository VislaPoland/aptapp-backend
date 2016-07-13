package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;

import java.net.MalformedURLException;
import java.net.URL;

public class ResetPasswordMessageTemplate implements EmailMessageTemplate {

    private final Account account;
    private final URL backendUrl;
    private final URL frontendUrl;

    public ResetPasswordMessageTemplate(Account account, URL backendUrl, URL frontendUrl) {
        this.account = account;
        this.backendUrl = backendUrl;
        this.frontendUrl = frontendUrl;
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

    public String getApplicationUrl() {
        return frontendUrl.toString();
    }

    public String getActivationToken() {
        return account.getActionToken();
    }

    public String getResetPasswordPageUrl() throws MalformedURLException {
        return new URL(frontendUrl, String.format("/set-password?token=%s", account.getActionToken())).toString();
    }

    public String getLogoUrl() throws MalformedURLException {
        return new URL(backendUrl, "/static/images/aptapp_logo.png").toString();
    }

    public String getIconUrl() throws MalformedURLException {
        return new URL(backendUrl, "/static/images/aptapp_icon.png").toString();
    }
}

