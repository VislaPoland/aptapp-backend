package com.creatix.domain.dto.notification.reporting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class NotificationReportAccountDto {
    private final Long id;
    private final String firstName;
    private final String lastName;
}
