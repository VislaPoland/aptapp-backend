package com.creatix.domain.dto.notification.reporting;

import com.creatix.domain.dto.apartment.BasicApartmentDto;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "Timestamp of creation of event", dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private final OffsetDateTime createdAt;
    @ApiModelProperty(value = "Timestamp of resolution", dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private final OffsetDateTime respondedAt;
    private final Long responseTime;
    private final Long resolutionTime;
    private final String status;
    @ApiModelProperty(value = "Response to notification")
    private final String response;

    @Setter
    private BasicApartmentDto targetApartment;

    @Setter
    private NotificationReportAccountDto createdBy;

    @Setter
    @ApiModelProperty(value = "one who responded / confirmed notification")
    private NotificationReportAccountDto respondedBy;

    @Setter
    private NotificationReportAccountDto resolvedBy;
}
