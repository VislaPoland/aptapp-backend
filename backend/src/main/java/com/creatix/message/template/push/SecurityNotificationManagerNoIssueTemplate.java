package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.SecurityNotification;

public class SecurityNotificationManagerNoIssueTemplate extends SecurityNotificationTemplate {
    public SecurityNotificationManagerNoIssueTemplate(SecurityNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return "security-notification-manager-no-issue";
    }
}
