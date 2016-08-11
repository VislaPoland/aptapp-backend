package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@ApiModel
@Data
public class SlotUnitDto {
    @ApiModelProperty(value = "Slot unit ID", required = true)
    private long id;
    @ApiModelProperty(value = "Slot ID", required = true)
    private long slotId;
    @ApiModelProperty(value = "Begin time of the slot unit", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(value = "Start time of the slot unit", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime endTime;
    @ApiModelProperty(value = "Current capacity (1 = 1 person capacity)", required = true)
    private int capacity;
    @ApiModelProperty(value = "Initial capacity", required = true)
    private int initialCapacity;
}
