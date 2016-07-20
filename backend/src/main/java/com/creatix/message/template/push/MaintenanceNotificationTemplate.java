package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.MaintenanceNotification;

public class MaintenanceNotificationTemplate extends PushMessageTemplate {
    protected final MaintenanceNotification notification;

    public MaintenanceNotificationTemplate(MaintenanceNotification notification) {
        this.notification = notification;
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

    @Override
    public String getTemplateName() {
        return "maintenance-notification";
    }
}
