package com.creatix.domain.dto.tenant.parkingStall;

import com.creatix.domain.dto.tenant.vehicle.AbstractVehicleDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class ParkingStallDto extends AbstractParkingStallDto {
    @ApiModelProperty(value = "Parking vehicle")
    private VehicleDto parkingVehicle;

    @ApiModel
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class VehicleDto extends AbstractVehicleDto {
    }
}
