package com.creatix.message.template;

public interface SmsMessageTemplate extends MessageTemplate {

    /**
     * Phone number of the recipient.
     * @return valid phone number
     */
    String getRecipient();

}
