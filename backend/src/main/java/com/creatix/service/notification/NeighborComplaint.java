package com.creatix.service.notification;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;

import javax.annotation.Nonnull;

/**
 * Created by Tomas Sedlak on 19.10.2017.
 */
class NeighborComplaint {
    private final long neighborhoodNotificationId;

    public NeighborComplaint(@Nonnull NeighborhoodNotification notification) {
        this.neighborhoodNotificationId = notification.getId();
    }

    public long getNeighborhoodNotificationId() {
        return neighborhoodNotificationId;
    }
}
