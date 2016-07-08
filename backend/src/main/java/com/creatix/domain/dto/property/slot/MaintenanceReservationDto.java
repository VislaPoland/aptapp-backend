package com.creatix.domain.dto.property.slot;

import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@ApiModel
@Data
public class MaintenanceReservationDto {
    @ApiModelProperty(value = "Reservation ID", required = true)
    private long id;
    @ApiModelProperty(value = "Account ID", required = true)
    private Long accountId;
    @ApiModelProperty(value = "Property ID", required = true)
    private Long propertyId;
    @ApiModelProperty(value = "Reserved slot units", required = true)
    private List<SlotUnitDto> units;
    @ApiModelProperty(value = "Reserved slot", required = true)
    private SlotDto slot;
    @ApiModelProperty(value = "Reservation begin time", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(value = "Reservation end time", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime endTime;
    @ApiModelProperty(value = "Reserved capacity", required = true)
    private int capacity;
    @ApiModelProperty(value = "Reservation duration in minutes", required = true)
    private int durationMinutes;
    @ApiModelProperty(value = "Reservation note")
    private String note;
    @ApiModelProperty(value = "Notification", required = true)
    private MaintenanceNotificationDto notification;
}
