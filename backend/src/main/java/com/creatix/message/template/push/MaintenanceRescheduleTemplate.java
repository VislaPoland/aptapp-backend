package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.enums.ReservationStatus;

import java.util.Optional;


public class MaintenanceRescheduleTemplate extends MaintenanceNotificationTemplate {

    private final MaintenanceReservation reservation;

    public MaintenanceRescheduleTemplate(MaintenanceNotification notification) {
        super(notification);

        Optional<MaintenanceReservation> reservation = notification.getReservations().stream()
                .max((r1, r2) -> Long.compare(r1.getId(), r2.getId()));
        if ( reservation.isPresent() ) {
            this.reservation = reservation.get();

            if ( this.reservation.getStatus() != ReservationStatus.Rescheduled ) {
                throw new IllegalStateException("Invalid status of reservation: " + this.reservation.getStatus());
            }
        }
        else {
            throw new IllegalStateException("Not reservation is bound to notification");
        }
    }

    public String getFrom() {
        return formatTimestamp(reservation.getBeginTime());
    }

    public String getTo() {
        return formatTimestamp(reservation.getRescheduleTime());
    }

    @Override
    public String getTemplateName() {
        return "maintenance-reschedule";
    }
}
