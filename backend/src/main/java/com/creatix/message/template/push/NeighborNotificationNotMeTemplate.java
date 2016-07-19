package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;

public class NeighborNotificationNotMeTemplate extends NeighborNotificationTemplate {

    public NeighborNotificationNotMeTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return "neighbor-notification-not-me";
    }
}
