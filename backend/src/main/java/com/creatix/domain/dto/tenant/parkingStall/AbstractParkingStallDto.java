package com.creatix.domain.dto.tenant.parkingStall;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public abstract class AbstractParkingStallDto {
    @ApiModelProperty(value = "Parking stall ID", required = true)
    private Long id;
    @ApiModelProperty(value = "Parking stall number", required = true)
    private String number;
}
