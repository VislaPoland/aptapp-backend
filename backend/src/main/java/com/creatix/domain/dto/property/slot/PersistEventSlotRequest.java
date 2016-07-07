package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@ApiModel
@Data
public class PersistEventSlotRequest {
    @ApiModelProperty(value = "Time when slot starts", required = true, example = "2016-07-07T10:37:47.960Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(value = "Slot duration in minutes", required = true)
    private int durationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 person capacity)", required = true)
    private int initialCapacity;
}
