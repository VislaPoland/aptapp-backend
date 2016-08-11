package com.creatix.message.template.push;

import com.creatix.domain.entity.store.EventSlot;

public class EventNotificationAdjustTemplate extends EventNotificationTemplate {

    public EventNotificationAdjustTemplate(EventSlot event) {
        super(event);
    }

    @Override
    public String getTemplateName() {
        return "event-notification-adjust";
    }
}
