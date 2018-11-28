package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.MaintenanceNotification;

public class MaintenanceCompleteTemplate extends PushMessageTemplate {

    protected final MaintenanceNotification notification;

    public MaintenanceCompleteTemplate(MaintenanceNotification notification) {
        this.notification = notification;
    }

    public String getNotificationTitle() {
        return notification.getTitle();
    }

    @Override
    public String getTemplateName() {
        return "maintenance-complete";
    }
}
