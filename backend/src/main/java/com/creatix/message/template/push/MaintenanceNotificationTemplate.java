package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.MaintenanceNotification;

public class MaintenanceNotificationTemplate extends PushMessageTemplate {
    protected final MaintenanceNotification notification;

    public MaintenanceNotificationTemplate(MaintenanceNotification notification) {
        this.notification = notification;
    }

    public String getName() {
        return notification.getAuthor().getFullName();
    }

    public String getTime() {
        return formatTime(notification.getCreatedAt(), notification.getProperty().getZoneId());
    }

    public String getDate() {
        return formatDate(notification.getCreatedAt(), notification.getProperty().getZoneId());
    }

    public String getUnitNumber() {
        if (notification.getTargetApartment() != null) {
            return notification.getTargetApartment().getUnitNumber();
        }
        return null;
    }

    public String getNotificationTitle() {
        return notification.getTitle();
    }

    // in case of employee sender (we don't have apartment number for employee) send template for employee
    @Override
    public String getTemplateName() {
        return getUnitNumber() == null ? "maintenance-notification-by-employee" : "maintenance-notification-by-tenant";
    }
}
