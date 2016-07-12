package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@ApiModel
@Data
public abstract class SlotDto {

    public enum SlotType {
        Event, Maintenance
    }

    @ApiModelProperty(value = "Slot ID", required = true)
    private Long id;
    @ApiModelProperty(value = "Begin time of the slot", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(value = "Start time of the slot", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime endTime;
    @ApiModelProperty(value = "Duration of the slot units", required = true)
    private int unitDurationMinutes;
    @ApiModelProperty(value = "Slot units", required = true)
    private List<SlotUnitDto> units;

    @ApiModelProperty(value = "Slot type", required = true)
    public abstract SlotType getType();
}
