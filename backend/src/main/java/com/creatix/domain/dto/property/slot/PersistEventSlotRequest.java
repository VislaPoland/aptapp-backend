package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Date;

@ApiModel
@Data
public class PersistEventSlotRequest {
    @ApiModelProperty(value = "Time when slot starts", required = true)
    private OffsetDateTime beginTime;
    @ApiModelProperty(value = "Slot duration in minutes", required = true)
    private int durationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 person capacity)", required = true)
    private int initialCapacity;
}
