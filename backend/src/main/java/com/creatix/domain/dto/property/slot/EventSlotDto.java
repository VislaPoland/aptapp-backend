package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class EventSlotDto extends SlotDto {
    @ApiModelProperty(value = "Title", required = true)
    private String title;
    @ApiModelProperty(value = "Description")
    private String description;

    @Override
    public SlotType getType() {
        return SlotType.Event;
    }
}
