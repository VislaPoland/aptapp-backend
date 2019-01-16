package com.creatix.domain.dto.notification.reporting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class NotificationReportGlobalInfoDto {
    private final Long requests;
    private final Double openRequests;
    private final Long pastDueDateRequests;
    private final Double averageTimeToResponse;
    private final Double averageTimeToResolve;
}
