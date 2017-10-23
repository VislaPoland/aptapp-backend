package com.creatix.service.notification;

import com.creatix.domain.entity.store.notification.EscalatedNeighborhoodNotification;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Created by Tomas Sedlak on 23.10.2017.
 */
class Escalation {

    private final long escalationNotificationId;

    Escalation(@Nonnull EscalatedNeighborhoodNotification notification) {
        Objects.requireNonNull(notification.getId(), "Missing escalation notification ID");
        this.escalationNotificationId = notification.getId();
    }

    long getEscalationNotificationId() {
        return escalationNotificationId;
    }
}
