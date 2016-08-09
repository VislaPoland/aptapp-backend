package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;

import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceRescheduleConfirmTemplate extends MaintenanceNotificationTemplate {
    public MaintenanceRescheduleConfirmTemplate(MaintenanceNotification notification) {
        super(notification);
    }

    public String getTime() {
        List<MaintenanceReservation> reservations = notification.getReservations().stream()
                .sorted((r1, r2) -> Long.compare(r1.getId(), r2.getId()))
                .collect(Collectors.toList());
        MaintenanceReservation from = reservations.get(reservations.size() - 1);
        return formatTimestamp(from.getRescheduleTime());
    }

    @Override
    public String getTemplateName() {
        return "maintenance-reschedule-confirm";
    }
}
