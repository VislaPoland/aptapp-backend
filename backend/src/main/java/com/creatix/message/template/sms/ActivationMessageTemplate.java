package com.creatix.message.template.sms;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.creatix.configuration.BitlyProperties;
import com.creatix.message.MessageDeliveryException;

import net.swisstech.bitly.BitlyClient;
import net.swisstech.bitly.model.Response;
import net.swisstech.bitly.model.v3.ShortenResponse;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
public class ActivationMessageTemplate implements SmsMessageTemplate {


    @Nonnull
    private final String shortUrl;

    @Nonnull
    private final String recipient;

    public ActivationMessageTemplate(@Nonnull String shortUrl, @Nonnull String recipient) {
        this.shortUrl = shortUrl;
        this.recipient = recipient;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public String getTemplateName() {
        return "activation-account";
    }

    public String getActivationUrl() {
        return shortUrl;
    }
}
