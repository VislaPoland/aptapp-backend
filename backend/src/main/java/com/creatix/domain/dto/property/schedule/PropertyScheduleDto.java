package com.creatix.domain.dto.property.schedule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class PropertyScheduleDto {
    @ApiModelProperty(value = "Start hour of schedule time", required = true, notes = "24-hour clock")
    private Integer startHour;
    @ApiModelProperty(value = "Start minute of schedule time", required = true)
    private Integer startMinute;
    @ApiModelProperty(value = "End hour of schedule time", required = true, notes = "24-hour clock")
    private Integer endHour;
    @ApiModelProperty(value = "End minute of schedule time", required = true)
    private Integer endMinute;
    @ApiModelProperty(value = "Length of the period in minutes", required = true, notes = "This needs to be a divisor of the working time")
    private Integer periodLength;
    @ApiModelProperty(value = "Number of slots per period", required = true)
    private Integer slotsPerPeriod;
}
