package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.enums.PushNotificationTemplateName;

import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceRescheduleRejectTemplate extends PushMessageTemplate {

    private final MaintenanceReservation reservation;

    private final String staffName;

    public MaintenanceRescheduleRejectTemplate(MaintenanceReservation reservation, String staffName) {
        this.reservation = reservation;
        this.staffName = staffName;
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
        return staffName;
    }

    @Override
    public String getTemplateName() {
        return getUnitNumber() == null ? PushNotificationTemplateName.MAINTENANCE_RESCHEDULE_REJECT_WITHOUT_UNIT.getValue() : PushNotificationTemplateName.MAINTENANCE_RESCHEDULE_REJECT_WITH_UNIT.getValue();
    }
}