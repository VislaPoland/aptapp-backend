package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Account;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class ActivationMessageTemplate implements EmailMessageTemplate {

    private final Account account;
    private final URL backendUrl;
    private final URL frontendUrl;

    public ActivationMessageTemplate(Account account, URL backendUrl, URL frontendUrl) {
        this.account = account;
        this.backendUrl = backendUrl;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public String getSubject() {
        return "Welcome to Apt. – A smarter way to communicate";
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
        return frontendUrl.toString();
    }

    public String getLogoUrl() throws MalformedURLException {
        return new URL(backendUrl, "/static/images/aptapp_logo.png").toString();
    }

    public String getIconUrl() throws MalformedURLException {
        return new URL(backendUrl, "/static/images/aptapp_icon.png").toString();
    }
}
