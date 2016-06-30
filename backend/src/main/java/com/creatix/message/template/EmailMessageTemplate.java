package com.creatix.message.template;

public interface EmailMessageTemplate extends MessageTemplate {

    String getSubject();

    String getRecipient();

}
