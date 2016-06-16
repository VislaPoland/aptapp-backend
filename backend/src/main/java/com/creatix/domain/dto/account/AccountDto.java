package com.creatix.domain.dto.account;

import com.creatix.domain.dto.AddressDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Account data transfer object
 */
@ApiModel("Account DTO")
@Data
public class AccountDto {
    @ApiModelProperty(value = "Full name", required = true)
    private String fullName;

    @ApiModelProperty(value = "Email address", required = true)
    private String primaryEmail;

    @ApiModelProperty(value = "Address", required = true)
    private AddressDto address;

    @ApiModelProperty(value = "Associated property ID", required = false, notes = "This is required for tenant and pproperty manager")
    private Long propertyId;
}
