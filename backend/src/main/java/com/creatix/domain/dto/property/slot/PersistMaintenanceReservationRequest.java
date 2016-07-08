package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@ApiModel
@Data
public class PersistMaintenanceReservationRequest {
    @ApiModelProperty(required = true)
    private Long slotId;
    @ApiModelProperty("Maintenance notification ID")
    private Long notificationId;
    @ApiModelProperty(required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(required = true)
    private int durationMinutes;
    @ApiModelProperty(required = true)
    private int capacity = 1;
    @ApiModelProperty
    private String note;
}
