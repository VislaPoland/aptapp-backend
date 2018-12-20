package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.Account;

import java.net.MalformedURLException;

public abstract class ActivationMessageTemplate implements EmailMessageTemplate {

    protected final Account account;
    private final ApplicationProperties properties;

    public ActivationMessageTemplate(Account account, ApplicationProperties properties) {
        this.account = account;
        this.properties = properties;
    }

    @Override
    public String getSubject() {
        return "Welcome to Apt. App – A smarter way to communicate";
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

    public String getActivationPageUrl() throws MalformedURLException {
        return properties.buildAdminUrl(String.format("new-user/%s", account.getActionToken())).toString();
    }

    public String getLogoUrl() throws MalformedURLException {
        return properties.buildBackendUrl("static/aptapp_logo.png").toString();
    }

    public String getIconUrl() throws MalformedURLException {
        return properties.buildBackendUrl("static/aptapp_icon.png").toString();
    }
}
