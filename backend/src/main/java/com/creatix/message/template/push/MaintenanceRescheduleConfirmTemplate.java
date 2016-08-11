package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;

public class MaintenanceRescheduleConfirmTemplate extends PushMessageTemplate {

    private final MaintenanceReservation reservation;

    public MaintenanceRescheduleConfirmTemplate(MaintenanceReservation reservation) {
        this.reservation = reservation;
    }

    public String getTime() {
        return formatTimestamp(reservation.getBeginTime(), reservation.getSlot().getProperty().getZoneId());
    }

    @Override
    public String getTemplateName() {
        return "maintenance-reschedule-confirm";
    }
}
