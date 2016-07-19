package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.SecurityNotification;

public class SecurityNotificationNeighborResolvedTemplate extends SecurityNotificationTemplate {
    public SecurityNotificationNeighborResolvedTemplate(SecurityNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return "security-notification-neighbor-resolved";
    }
}
