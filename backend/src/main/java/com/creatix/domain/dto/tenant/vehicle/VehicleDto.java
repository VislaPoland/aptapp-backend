package com.creatix.domain.dto.tenant.vehicle;

import com.creatix.domain.dto.tenant.parkingStall.AbstractParkingStallDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class VehicleDto extends AbstractVehicleDto {
    @ApiModelProperty(value = "Parking stall")
    private ParkingStall parkingStall;

    @ApiModel
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ParkingStall extends AbstractParkingStallDto {
    }
}
