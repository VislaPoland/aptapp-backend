package com.creatix.message.template.sms;

import com.creatix.domain.entity.store.EventInvite;
import com.creatix.domain.enums.SmsTemplateName;

import javax.annotation.Nonnull;

public class RsvpReminderMessageTemplate implements SmsMessageTemplate {

    private final EventInvite invite;

    public RsvpReminderMessageTemplate(@Nonnull EventInvite invite) {
        this.invite = invite;
    }

    @Override
    public String getRecipient() {
        return invite.getAttendant().getPrimaryPhone();
    }

    @Override
    public String getTemplateName() {
        return SmsTemplateName.RSVP_REMINDER.getValue();
    }

    @Nonnull
    public String getEventTitle() {
        return invite.getEvent().getTitle();
    }
}
