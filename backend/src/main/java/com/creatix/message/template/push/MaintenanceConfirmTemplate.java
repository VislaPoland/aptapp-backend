package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;

public class MaintenanceConfirmTemplate extends PushMessageTemplate {

    private final MaintenanceReservation reservation;

    public MaintenanceConfirmTemplate(MaintenanceReservation reservation) {
        this.reservation = reservation;
    }

    public String getNotificationTitle() {
        return reservation.getNotification().getTitle();
    }

    @Override
    public String getTemplateName() {
        return "maintenance-confirm";
    }
}
