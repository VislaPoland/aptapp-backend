package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.SecurityNotification;
import com.creatix.domain.enums.AccountRole;

public class SecurityNotificationTemplate extends PushMessageTemplate {
    protected final SecurityNotification notification;

    public SecurityNotificationTemplate(SecurityNotification notification) {
        this.notification = notification;
    }

    @Override
    public String getTemplateName() {
        return "security-notification";
    }

    public String getRole() {
        return translateRoleNameFromEnum(notification.getAuthor().getRole());
    }

    public String getName() {
        return notification.getAuthor().getFullName();
    }

    public String getMessage() {
        return notification.getTitle();
    }

    public String getTimestamp() {
        return formatTimestamp(notification.getCreatedAt(), notification.getProperty().getZoneId());
    }

    protected String translateRoleNameFromEnum(AccountRole role) {
        switch ( role ) {
            case Tenant:
                return "Neighbor";
            case SubTenant:
                return "Neighbor";
            case AssistantPropertyManager:
                return "Property manager assistant";
            case PropertyManager:
                return "Property manager";
            default:
                return "";
        }
    }
}
