package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

@ApiModel
@Data
public class PersistMaintenanceReservationRequest {
    @ApiModelProperty(required = true)
    private Long slotId;
    @ApiModelProperty(required = true)
    private LocalTime beginTime;
    @ApiModelProperty(required = true)
    private int durationMinutes;
    @ApiModelProperty(required = true)
    private int capacity = 1;
    @ApiModelProperty
    private String note;
}
