package com.creatix.message.template.push;

import com.creatix.domain.entity.store.EventSlot;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class EventNotificationCancelTemplate extends EventNotificationTemplate {

    public EventNotificationCancelTemplate(EventSlot event) {
        super(event);
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.EVENT_NOTIFICATION_CANCEL.getValue();
    }
}
