package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("Address DTO")
@Data
public class AddressDto {   //TODO rework addressdto usage
    @ApiModelProperty(value = "House number", required = true)
    private String houseNumber;

    @ApiModelProperty(value = "Street name", required = true)
    private String streetName;

    @ApiModelProperty(value = "Name of town", required = true)
    private String town;

    @ApiModelProperty(value = "State abbreviation", required = true)
    private String state;

    @ApiModelProperty(value = "ZIP code", required = true)
    private String zipCode;
}
