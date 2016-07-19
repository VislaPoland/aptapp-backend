package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.SecurityNotification;

public class SecurityNotificationNeighborNoIssueTemplate extends SecurityNotificationTemplate {
    public SecurityNotificationNeighborNoIssueTemplate(SecurityNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return "security-notification-neighbor-no-issue";
    }
}
