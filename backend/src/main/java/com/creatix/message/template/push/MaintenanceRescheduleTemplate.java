package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;


public class MaintenanceRescheduleTemplate extends PushMessageTemplate {

    private final MaintenanceReservation reservationOld;
    private final MaintenanceReservation reservationNew;


    public MaintenanceRescheduleTemplate(MaintenanceReservation reservationOld, MaintenanceReservation reservationNew) {
        this.reservationOld = reservationOld;
        this.reservationNew = reservationNew;
    }

    public String getFrom() {
        return formatTimestamp(reservationOld.getBeginTime(), reservationOld.getSlot().getProperty().getZoneId());
    }

    public String getTo() {
        return formatTimestamp(reservationNew.getBeginTime(), reservationNew.getSlot().getProperty().getZoneId());
    }

    @Override
    public String getTemplateName() {
        return "maintenance-reschedule";
    }
}
