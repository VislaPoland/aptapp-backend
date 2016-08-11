package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;

import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceRescheduleRejectTemplate extends PushMessageTemplate {

    private final MaintenanceReservation reservation;

    public MaintenanceRescheduleRejectTemplate(MaintenanceReservation reservation) {
        this.reservation = reservation;
    }

    public String getTime() {
        return formatTimestamp(reservation.getBeginTime(), reservation.getSlot().getProperty().getZoneId());
    }

    @Override
    public String getTemplateName() {
        return "maintenance-reschedule-reject";
    }
}
