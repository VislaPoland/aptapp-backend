package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.Map;

@ApiModel
@Data
public class PersistMaintenanceSlotScheduleRequest {

    @NotNull
    @ApiModelProperty(value = "All days in week")
    private Map<DayOfWeek, DayDuration> durationPerDayOfWeek;
    @ApiModelProperty(value = "Slot unit duration in minutes", required = true)
    private int unitDurationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 employee capacity)", required = true)
    private int initialCapacity;
}
