package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.enums.PushNotificationTemplateName;


public class MaintenanceRescheduleTemplate extends PushMessageTemplate {

    private final MaintenanceReservation reservationOld;
    private final MaintenanceReservation reservationNew;


    public MaintenanceRescheduleTemplate(MaintenanceReservation reservationOld, MaintenanceReservation reservationNew) {
        this.reservationOld = reservationOld;
        this.reservationNew = reservationNew;
    }

    public String getTime() {
        return formatTime(reservationNew.getBeginTime(), reservationNew.getNotification().getProperty().getZoneId());
    }

    public String getDate() {
        return formatDate(reservationNew.getBeginTime(), reservationNew.getNotification().getProperty().getZoneId());
    }

    public String getNotificationTitle() {
        return reservationOld.getNotification().getTitle();
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.MAINTENANCE_RESCHEDULE.getValue();
    }
}
