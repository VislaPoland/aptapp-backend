package com.creatix.message.template.sms;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ActivationWebMessageTemplate implements SmsMessageTemplate {

    private final String shortUrl;
    private final String recipient;

    public ActivationWebMessageTemplate(String shortUrl, String recipient) {
        this.shortUrl = Objects.requireNonNull(shortUrl);
        this.recipient = Objects.requireNonNull(recipient);
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public String getTemplateName() {
        return "activation-account-web";
    }

    public String getActivationUrl() {
        return shortUrl;
    }
}
