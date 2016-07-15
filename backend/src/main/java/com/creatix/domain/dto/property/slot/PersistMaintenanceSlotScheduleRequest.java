package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@ApiModel
@Data
public class PersistMaintenanceSlotScheduleRequest {
    @NotNull
    @ApiModelProperty(value = "Time when slot starts", required = true, dataType = "java.lang.String", example = "09:00:00.000")
    private LocalTime beginTime;
    @NotNull
    @ApiModelProperty(value = "Time when slot ends", required = true, dataType = "java.lang.String", example = "17:00:00.000")
    private LocalTime endTime;
    @ApiModelProperty(value = "Days of week")
    private Set<DayOfWeek> daysOfWeek;
    @ApiModelProperty(value = "Slot unit duration in minutes", required = true)
    private int unitDurationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 employee capacity)", required = true)
    private int initialCapacity;
}
