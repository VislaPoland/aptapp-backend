package com.creatix.message.template.sms;

import javax.annotation.Nonnull;
import com.creatix.message.MessageDeliveryException;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
public class ActivationMessageTemplate implements SmsMessageTemplate {

    @Nonnull
    private final String recipient;

    @Nonnull
    private final String activationToken;

    public ActivationMessageTemplate(@Nonnull String activationToken, @Nonnull String recipient) {
        this.recipient = recipient;
        this.activationToken = activationToken;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    public String getActivationCode() {
        return activationToken;
    }

    @Override
    public String getTemplateName() {
        return "activation-account";
    }

}
