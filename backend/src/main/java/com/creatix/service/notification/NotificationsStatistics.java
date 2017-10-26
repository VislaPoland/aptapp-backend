package com.creatix.service.notification;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;

/**
 * Created by Tomas Sedlak on 26.10.2017.
 */
@Getter
@Setter
public class NotificationsStatistics {
    @Nonnull
    private OffsetDateTime dateFrom;
    @Nonnull
    private OffsetDateTime dateTo;

    private int notificationCount;

    private int escalationCount;
}
