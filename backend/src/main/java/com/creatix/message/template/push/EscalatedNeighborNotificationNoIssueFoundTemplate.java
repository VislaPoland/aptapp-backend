package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;

public class EscalatedNeighborNotificationNoIssueFoundTemplate extends NeighborNotificationTemplate {

    public EscalatedNeighborNotificationNoIssueFoundTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return "escalated-neighbor-notification-no-issue-found";
    }
}
