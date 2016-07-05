package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@ApiModel
@Data
public class SlotScheduleDto {
    @ApiModelProperty(value = "Time when slot starts", required = true)
    private LocalTime beginTime;
    @ApiModelProperty(value = "Time when slot ends", required = true)
    private LocalTime endTime;
    @ApiModelProperty(value = "Days of week", required = true)
    private Set<DayOfWeek> daysOfWeek;
    @ApiModelProperty(value = "Slot unit duration in minutes", required = true)
    private int unitDurationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 employee capacity)", required = true)
    private int initialCapacity;
    @ApiModelProperty(value = "Slot schedule target role", required = true, dataType = "List[java.lang.Long]", notes = "Slots will be shown only to accounts with same role.")
    private AccountRole targetRole;
}
