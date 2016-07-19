package com.creatix.message.template.email;

import com.creatix.message.template.MessageTemplate;

public interface EmailMessageTemplate extends MessageTemplate {

    String getSubject();

    String getRecipient();

}
