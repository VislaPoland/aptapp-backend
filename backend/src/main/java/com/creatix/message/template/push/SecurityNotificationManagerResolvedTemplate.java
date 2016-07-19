package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.SecurityNotification;

public class SecurityNotificationManagerResolvedTemplate extends SecurityNotificationTemplate {
    public SecurityNotificationManagerResolvedTemplate(SecurityNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return "security-notification-manager-resolved";
    }
}
