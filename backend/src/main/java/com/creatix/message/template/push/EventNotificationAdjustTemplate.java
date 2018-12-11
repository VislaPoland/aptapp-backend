package com.creatix.message.template.push;

import com.creatix.domain.entity.store.EventSlot;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class EventNotificationAdjustTemplate extends EventNotificationTemplate {

    public EventNotificationAdjustTemplate(EventSlot event) {
        super(event);
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.EVENT_NOTIFICATION_ADJUST.getValue();
    }
}
