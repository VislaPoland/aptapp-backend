package com.creatix.message.template.sms;

import com.creatix.message.template.MessageTemplate;

public interface SmsMessageTemplate extends MessageTemplate {

    /**
     * Phone number of the recipient.
     * @return valid phone number
     */
    String getRecipient();

}
