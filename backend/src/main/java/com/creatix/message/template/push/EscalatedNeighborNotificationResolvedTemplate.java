package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class EscalatedNeighborNotificationResolvedTemplate extends NeighborNotificationTemplate {

    public EscalatedNeighborNotificationResolvedTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.ESCALATED_NEIGHBOR_NOTIFICATION_RESOLVED.getValue();
    }
}
