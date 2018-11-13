package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;


public class MaintenanceRescheduleTemplate extends PushMessageTemplate {

    private final MaintenanceReservation reservationOld;
    private final MaintenanceReservation reservationNew;


    public MaintenanceRescheduleTemplate(MaintenanceReservation reservationOld, MaintenanceReservation reservationNew) {
        this.reservationOld = reservationOld;
        this.reservationNew = reservationNew;
    }

    public String getTime() {
        return formatTime(reservationNew.getNotification().getCreatedAt(), reservationNew.getNotification().getProperty().getZoneId());
    }

    public String getDate() {
        return formatDate(reservationNew.getNotification().getCreatedAt(), reservationNew.getNotification().getProperty().getZoneId());
    }

    public String getNotificationTitle() {
        return reservationOld.getNotification().getTitle();
    }

    @Override
    public String getTemplateName() {
        return "maintenance-reschedule";
    }
}
