package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;

public class NeighborNotificationResolvedTemplate extends NeighborNotificationTemplate {

    public NeighborNotificationResolvedTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return "neighbor-notification-resolved";
    }
}
