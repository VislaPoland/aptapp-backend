package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@ApiModel
@Data
@Accessors(chain = true)
public class BusinessContactDto {

    @ApiModelProperty("Contact entry id")
    private Long id;

    @ApiModelProperty("Contact street")
    private String street;

    @ApiModelProperty("House number")
    private String houseNumber;

    @ApiModelProperty("Zip code")
    private String zipCode;

    @ApiModelProperty("Country")
    private String country;

    @ApiModelProperty("State")
    private String state;
}
