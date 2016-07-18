package com.creatix.message.template;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.Account;

import java.net.MalformedURLException;
import java.net.URL;

public class ResetPasswordMessageTemplate implements EmailMessageTemplate {

    private final Account account;
    private final ApplicationProperties properties;

    public ResetPasswordMessageTemplate(Account account, ApplicationProperties properties) {
        this.account = account;
        this.properties = properties;
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
        return properties.getAdminUrl().toString();
    }

    public String getWebPageUrl() {
        return properties.getFrontendUrl().toString();
    }

    public String getResetPasswordPageUrl() throws MalformedURLException {
        return properties.buildAdminUrl(String.format("set-password?token=%s", account.getActionToken())).toString();
    }

    public String getLogoUrl() throws MalformedURLException {
        return properties.buildBackendUrl("static/aptapp_logo.png").toString();
    }

    public String getIconUrl() throws MalformedURLException {
        return properties.buildBackendUrl("static/aptapp_icon.png").toString();
    }
}

