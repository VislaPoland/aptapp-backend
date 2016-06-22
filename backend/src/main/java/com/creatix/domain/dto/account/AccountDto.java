package com.creatix.domain.dto.account;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.ApartmentDto;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Account data transfer object
 */
@ApiModel("Account DTO")
@Data
public class AccountDto {

    @ApiModelProperty(value = "Account ID", required = true)
    private Long id;

    @ApiModelProperty(value = "Full name", required = true)
    private String fullName;

    @ApiModelProperty(value = "Email address", required = true)
    private String primaryEmail;

    @ApiModelProperty(value = "Address", required = true)
    private AddressDto address;

    @ApiModelProperty(value = "Associated property details", notes = "This is required for tenant and property manager")
    private PropertyDetailsDto property;

    @ApiModelProperty(value = "Associated apartment")
    private ApartmentDto apartment;
}
