package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class NeighborNotificationResolvedTemplate extends NeighborNotificationTemplate {

    public NeighborNotificationResolvedTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.NEIGHBOR_NOTIFICATION_RESOLVED.getValue();
    }
}
