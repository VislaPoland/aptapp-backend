package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.enums.PushNotificationTemplateName;

public class MaintenanceRescheduleConfirmTemplate extends PushMessageTemplate {

    private final MaintenanceReservation reservation;

    private final String fullName;

    public MaintenanceRescheduleConfirmTemplate(MaintenanceReservation reservation, String fullName) {
        this.reservation = reservation;
        this.fullName = fullName;
    }

    public String getTime() {
        return formatTime(reservation.getBeginTime(), reservation.getSlot().getProperty().getZoneId());
    }

    public String getDate() {
        return formatDate(reservation.getBeginTime(), reservation.getSlot().getProperty().getZoneId());
    }

    public String getUnitNumber() {
        if (reservation.getNotification().getTargetApartment() != null) {
            return reservation.getNotification().getTargetApartment().getUnitNumber();
        }
        return null;
    }

    public String getNotificationTitle() {
        return reservation.getNotification().getTitle();
    }

    public String getStaffName() {
        return fullName;
    }

    @Override
    public String getTemplateName() {
        return (getUnitNumber() == null) ? PushNotificationTemplateName.MAINTENANCE_RESCHEDULE_CONFIRM_WITHOUT_UNIT.getValue() : PushNotificationTemplateName.MAINTENANCE_RESCHEDULE_CONFIRM_WITH_UNIT.getValue();
    }
}