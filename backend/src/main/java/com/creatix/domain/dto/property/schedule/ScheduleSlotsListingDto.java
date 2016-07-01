package com.creatix.domain.dto.property.schedule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel
@Data
public class ScheduleSlotsListingDto {
    @ApiModelProperty(value = "Slots listing in time given interval")
    private List<ScheduleSlot> slots;

    @ApiModel
    @Data
    public static class ScheduleSlot {
        @ApiModelProperty(value = "Starting date of the slot", required = true)
        private Date slotStart;
        @ApiModelProperty(value = "Slot usability", required = true)
        private boolean free;
    }
}
