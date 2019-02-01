package com.creatix.message.template.sms;

import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.enums.SmsTemplateName;
import com.creatix.message.template.sms.SmsMessageTemplate;

public class NeighborNotificationTemplate implements SmsMessageTemplate {

    private final Tenant recipient;

    public NeighborNotificationTemplate(Tenant recipient) {
        this.recipient = recipient;
    }

    @Override
    public String getRecipient() {
        return recipient.getPrimaryPhone();
    }

    @Override
    public String getTemplateName() {
        return SmsTemplateName.NEIGHBOR_NOTIFICATION.getValue();
    }
}
