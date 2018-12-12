package com.creatix.message.template.sms;

import com.creatix.domain.entity.store.notification.PersonalMessage;
import com.creatix.domain.enums.SmsTemplateName;

/**
 * Created by kvimbi on 01/06/2017.
 */
public class TenantPersonalMessageTemplate implements SmsMessageTemplate {

    private final String recipient;
    private final PersonalMessage personalMessage;

    public TenantPersonalMessageTemplate(String recipient, PersonalMessage personalMessage) {
        this.recipient = recipient;
        this.personalMessage = personalMessage;
    }

    @Override
    public String getTemplateName() {
        return SmsTemplateName.PERSONAL_MESSAGE.getValue();
    }

    public String getMessageContent() {
        return this.personalMessage.getContent();
    }

    @Override
    public String getRecipient() {
        return this.recipient;
    }
}
