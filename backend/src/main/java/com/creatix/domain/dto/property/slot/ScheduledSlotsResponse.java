package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class ScheduledSlotsResponse {
    @ApiModelProperty(value = "Slots", required = true)
    private List<SlotDto> slots;
    @ApiModelProperty(value = "Next slot ID")
    private Long nextId;
}
