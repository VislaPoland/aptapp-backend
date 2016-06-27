package com.creatix.domain.dto.tenant.parkingStall;

import com.creatix.domain.dto.tenant.vehicle.AbstractVehicleDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class ParkingStallDto extends AbstractParkingStallDto {
    private VehicleDto parkingVehicle;

    @ApiModel
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class VehicleDto extends AbstractVehicleDto {
    }
}
