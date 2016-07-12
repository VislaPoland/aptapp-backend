package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel
@Getter
@Setter
public class MaintenanceSlotDto extends SlotDto {
    @ApiModelProperty(value = "Slot reservations")
    private List<MaintenanceReservationDto> reservations;

    @Override
    public SlotType getType() {
        return SlotType.Maintenance;
    }
}
