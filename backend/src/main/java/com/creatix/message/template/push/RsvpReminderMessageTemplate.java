package com.creatix.message.template.push;

import com.creatix.domain.entity.store.EventInvite;

import javax.annotation.Nonnull;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
public class RsvpReminderMessageTemplate extends PushMessageTemplate {

    private final EventInvite invite;

    public RsvpReminderMessageTemplate(@Nonnull EventInvite invite) {
        this.invite = invite;
    }

    @Override
    public String getTemplateName() {
        return "rsvp-reminder";
    }

    @Nonnull
    public String getEventTitle() {
        return invite.getEvent().getTitle();
    }
}
