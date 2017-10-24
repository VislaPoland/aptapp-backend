package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;

public class EscalatedNeighborNotificationResolvedTemplate extends NeighborNotificationTemplate {

    public EscalatedNeighborNotificationResolvedTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return "escalated-neighbor-notification-resolved";
    }
}
