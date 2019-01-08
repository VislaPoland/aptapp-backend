package com.creatix.domain.dto.notification.reporting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@ToString
@Getter
public class NotificationReportDto {

    private final Long id;
    private final String title;
    private final String description;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime respondedAt;
    private final Long responseTime;
    private final Long resolutionTime;
    private final String status;

    @Setter
    private NotificationReportAccountDto createdBy;

    @Setter
    private NotificationReportAccountDto respondedBy;

    @Setter
    private NotificationReportAccountDto resolvedBy;
}
