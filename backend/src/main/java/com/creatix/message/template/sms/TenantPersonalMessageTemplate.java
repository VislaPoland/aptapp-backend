package com.creatix.message.template.sms;

import com.creatix.domain.entity.store.notification.PersonalMessage;
import com.creatix.domain.enums.SmsTemplateName;

/**
 * Created by kvimbi on 01/06/2017.
 */
public class TenantPersonalMessageTemplate implements SmsMessageTemplate {

    private final String recipient;
    private final String propertyName;

    public TenantPersonalMessageTemplate(String recipient, String propertyName) {
        this.recipient = recipient;
        this.propertyName = propertyName;
    }

    @Override
    public String getTemplateName() {
        return SmsTemplateName.PERSONAL_MESSAGE.getValue();
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public String getRecipient() {
        return this.recipient;
    }
}
