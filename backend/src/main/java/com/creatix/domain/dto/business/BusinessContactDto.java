package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@ApiModel
@Data
public class BusinessContactDto {

    @ApiModelProperty("Contact street")
    private String street;

    @ApiModelProperty("House number")
    private String houseNumber;

    @ApiModelProperty("Zip code")
    private Integer zipCode;

    @ApiModelProperty("Country")
    private String country;

    @ApiModelProperty("State")
    private String state;
}
