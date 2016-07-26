package com.creatix.domain.dto.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class VehicleDto {

    @ApiModelProperty(value = "Vehicle ID")
    private Long id;

    @NotEmpty
    @ApiModelProperty(required = true, value = "License plate number")
    private String licensePlate;

    @NotEmpty
    @ApiModelProperty(required = true, value = "Vehicle manufacturer")
    private String make;

    @NotEmpty
    @ApiModelProperty(required = true, value = "Vehicle model")
    private String model;

    @NotNull
    @ApiModelProperty(required = true, value = "Vehicle made year")
    private Integer year;

    @NotEmpty
    @ApiModelProperty(required = true, value = "Hex code of vehicle's color")
    private String color;
}
