package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.SecurityNotification;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class SecurityNotificationManagerNoIssueTemplate extends SecurityNotificationTemplate {
    public SecurityNotificationManagerNoIssueTemplate(SecurityNotification notification) {
        super(notification);
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.SECURITY_NOTIFICATION_MANAGER_NO_ISSUE.getValue();
    }
}
