package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class EscalatedNeighborNotificationNoIssueFoundTemplate extends NeighborNotificationTemplate {

    public EscalatedNeighborNotificationNoIssueFoundTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.ESCALATED_NEIGHBOR_NOTIFICATION_NO_ISSUE.getValue();
    }
}
