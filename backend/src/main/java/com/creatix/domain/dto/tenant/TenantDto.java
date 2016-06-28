package com.creatix.domain.dto.tenant;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.ApartmentDto;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.enums.TenantType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class TenantDto {
    @ApiModelProperty(value = "Account ID", required = true)
    private Long id;

    @ApiModelProperty(value = "First name", required = true)
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    private String lastName;

    @ApiModelProperty(value = "Company name", required = true)
    private String companyName;

    @ApiModelProperty(value = "Primary phone number", required = true)
    private String primaryPhone;

    @ApiModelProperty(value = "Primary email address", required = true)
    private String primaryEmail;

    @ApiModelProperty(value = "Secondary email address")
    private String secondaryEmail;

    @ApiModelProperty(value = "Secondary phone number")
    private String secondaryPhone;

    @ApiModelProperty(value = "Tenant type", required = true)
    private TenantType type;

    @ApiModelProperty(value = "Address", required = true)
    private AddressDto address;

    @ApiModelProperty(value = "Associated property details", required = true)
    private PropertyDetailsDto property;

    @ApiModelProperty(value = "Associated apartment", required = true)
    private ApartmentDto apartment;
    //TODO subtenants
}
