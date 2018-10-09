package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApiModel
@Data
public class PersistMaintenanceSlotScheduleRequest {

    @NotNull
    @ApiModelProperty(value = "Days of week")
    private List<DayDuration> daysOfWeek;
    @ApiModelProperty(value = "Slot unit duration in minutes", required = true)
    private int unitDurationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 employee capacity)", required = true)
    private int initialCapacity;
}
