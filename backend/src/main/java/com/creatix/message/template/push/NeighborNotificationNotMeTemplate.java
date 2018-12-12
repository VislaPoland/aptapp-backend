package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class NeighborNotificationNotMeTemplate extends NeighborNotificationTemplate {

    public NeighborNotificationNotMeTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.NEIGHBOR_NOTIFICATION_NOT_ME.getValue();
    }
}
