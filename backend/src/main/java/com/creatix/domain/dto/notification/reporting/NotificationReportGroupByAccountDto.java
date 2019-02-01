package com.creatix.domain.dto.notification.reporting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class NotificationReportGroupByAccountDto {

    private final Long confirmed;
    private final Long resolved;
    private final Double averageTimeToResponse;
    private final Double averageTimeToResolve;

    @Setter
    private NotificationReportAccountDto account;

}
