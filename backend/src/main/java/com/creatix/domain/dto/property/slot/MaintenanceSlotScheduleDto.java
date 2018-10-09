package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@ApiModel
@Data
public class MaintenanceSlotScheduleDto {
    @ApiModelProperty(value = "Slot unit duration in minutes", required = true)
    private int unitDurationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 employee capacity)", required = true)
    private int initialCapacity;
    @ApiModelProperty(value = "Days of week with adjustable work time", required = true)
    private Set<DurationPerDayOfWeekDto> durationPerDayOfWeek;
}
