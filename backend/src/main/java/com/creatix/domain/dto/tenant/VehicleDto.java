package com.creatix.domain.dto.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@EqualsAndHashCode(of = {"id"})
public class VehicleDto {

    @ApiModelProperty(value = "Vehicle ID")
    private Long id;

    @ApiModelProperty(value = "Vehicle manufacturer")
    private String make;

    @ApiModelProperty(value = "Vehicle model")
    private String model;

    @ApiModelProperty(value = "Vehicle made year")
    private Integer year;

    @ApiModelProperty(value = "Hex code of vehicle's color")
    private String color;
}
