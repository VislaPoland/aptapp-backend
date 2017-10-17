package com.creatix.message.template.sms;

import com.creatix.domain.entity.store.account.Account;

public class RsvpReminderMessageTemplate implements SmsMessageTemplate {

    private final Account recipient;

    public RsvpReminderMessageTemplate(Account recipient) {
        this.recipient = recipient;
    }

    @Override
    public String getRecipient() {
        return recipient.getPrimaryPhone();
    }

    @Override
    public String getTemplateName() {
        return "rsvp-reminder";
    }
}
