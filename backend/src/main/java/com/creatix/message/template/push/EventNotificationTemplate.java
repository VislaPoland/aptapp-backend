package com.creatix.message.template.push;

import com.creatix.domain.entity.store.EventSlot;

public class EventNotificationTemplate extends PushMessageTemplate {
    protected final EventSlot event;

    public EventNotificationTemplate(EventSlot event) {
        this.event = event;
    }

    public String getName() {
        return event.getTitle();
    }

    public String getTimestamp() {
        return formatTimestamp(event.getBeginTime());
    }

    @Override
    public String getTemplateName() {
        return "event-notification";
    }
}
