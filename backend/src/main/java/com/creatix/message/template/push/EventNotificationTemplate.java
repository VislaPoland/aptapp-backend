package com.creatix.message.template.push;

import com.creatix.domain.entity.store.EventSlot;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class EventNotificationTemplate extends PushMessageTemplate {
    protected final EventSlot event;

    public EventNotificationTemplate(EventSlot event) {
        this.event = event;
    }

    public String getName() {
        return event.getTitle();
    }

    public String getTimestamp() {
        return formatTimestamp(event.getBeginTime(), event.getProperty().getZoneId());
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.EVENT_NOTIFICATION.getValue();
    }
}
