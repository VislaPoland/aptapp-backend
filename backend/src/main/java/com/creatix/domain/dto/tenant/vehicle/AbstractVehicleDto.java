package com.creatix.domain.dto.tenant.vehicle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public abstract class AbstractVehicleDto {
    @NotNull
    @ApiModelProperty(required = true, value = "License plate number")
    private String licensePlate;

    @NotNull
    @ApiModelProperty(required = true, value = "Vehicle manufacturer")
    private String make;

    @NotNull
    @ApiModelProperty(required = true, value = "Vehicle model")
    private String model;

    @NotNull
    @ApiModelProperty(required = true, value = "Vehicle made year")
    private Integer year;

    @NotNull
    @ApiModelProperty(required = true, value = "Hex code of vehicle's color")
    private String color;
}
