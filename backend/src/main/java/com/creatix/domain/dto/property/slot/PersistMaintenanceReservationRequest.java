package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.ReservationStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@ApiModel
@Data
public class PersistMaintenanceReservationRequest {
    @NotNull
    @ApiModelProperty(required = true)
    private Long slotId;
    @NotNull
    @ApiModelProperty("Maintenance notification ID")
    private Long notificationId;
    @NotNull
    @ApiModelProperty(required = true, dataType = "java.lang.String", example = "2016-07-06T11:12:30.000Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(required = true)
    private int durationMinutes;
    @ApiModelProperty(required = true)
    private int capacity = 1;
    @ApiModelProperty
    private String note;
    @NotNull
    @ApiModelProperty(required = true)
    private ReservationStatus status;
    @ApiModelProperty(dataType = "java.lang.String", example = "2016-07-09T11:00:00.000Z")
    private OffsetDateTime rescheduleTime;
}
