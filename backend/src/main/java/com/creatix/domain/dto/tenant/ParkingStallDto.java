package com.creatix.domain.dto.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ParkingStallDto {
    @ApiModelProperty(value = "Parking stall ID")
    private Long id;
    @ApiModelProperty(value = "Parking stall number", required = true)
    private String number;
}
