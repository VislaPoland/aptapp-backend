package com.creatix.message.template.push;

import com.creatix.domain.entity.store.EventSlot;

public class EventNotificationCancelTemplate extends EventNotificationTemplate {

    public EventNotificationCancelTemplate(EventSlot event) {
        super(event);
    }

    @Override
    public String getTemplateName() {
        return "event-notification-cancel";
    }
}
