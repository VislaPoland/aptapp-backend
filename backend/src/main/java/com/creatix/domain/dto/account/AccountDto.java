package com.creatix.domain.dto.account;

import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.property.PropertyDto;
import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Account data transfer object
 */
@ApiModel
@Getter
@Setter
public class AccountDto {

    @ApiModelProperty(value = "Account ID", required = true)
    private Long id;

    @ApiModelProperty(value = "Role", required = true)
    private AccountRole role;

    @ApiModelProperty(value = "First name", required = true)
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    private String lastName;

    @ApiModelProperty(value = "Full name", required = true)
    private String fullName;

    @ApiModelProperty(value = "Email address", required = true)
    private String primaryEmail;

    @ApiModelProperty(value = "Secondary email address", required = true)
    private String secondaryEmail;

    @ApiModelProperty(value = "Phone number", required = true)
    private String primaryPhone;

    @ApiModelProperty(value = "Secondary phone number", required = true)
    private String secondaryPhone;

    @ApiModelProperty(value = "Company name")
    private String companyName;

    @ApiModelProperty(value = "Associated property details", notes = "This is required for tenant and property manager")
    private PropertyDto property;

    @ApiModelProperty(value = "Associated apartment")
    private ApartmentDto apartment;

    @ApiModelProperty(value = "Owned properties")
    private List<PropertyDto> ownedProperties;
}
