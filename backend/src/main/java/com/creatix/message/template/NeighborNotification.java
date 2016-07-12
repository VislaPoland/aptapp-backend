package com.creatix.message.template;

import com.creatix.domain.entity.store.account.Tenant;

public class NeighborNotification implements SmsMessageTemplate {

    private final Tenant recipient;

    public NeighborNotification(Tenant recipient) {
        this.recipient = recipient;
    }

    @Override
    public String getRecipient() {
        return recipient.getPrimaryPhone();
    }

    @Override
    public String getTemplateName() {
        return "neighbor-notification";
    }
}
