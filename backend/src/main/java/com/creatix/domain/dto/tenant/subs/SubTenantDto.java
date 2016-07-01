package com.creatix.domain.dto.tenant.subs;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.enums.TenantType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class SubTenantDto {
    @ApiModelProperty(value = "Sub-tenant ID", required = true)
    private Long id;

    @ApiModelProperty(value = "First name", required = true)
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    private String lastName;

    @ApiModelProperty(value = "Full name", required = true)
    private String fullName;

    @ApiModelProperty(value = "Phone number", required = true)
    private String phone;

    @ApiModelProperty(value = "Email address", required = true)
    private String email;

    @ApiModelProperty(value = "Company name")
    private String companyName;

    @ApiModelProperty(value = "Tenant type", required = true)
    private TenantType type;

    @ApiModelProperty(value = "Parent tenant ID", required = true)
    private Long parentTenantId;

    @ApiModelProperty(value = "Address", required = true)
    private AddressDto address;

    @ApiModelProperty(value = "Associated property details", required = true)
    private PropertyDetailsDto property;

    @ApiModelProperty(value = "Associated apartment", required = true)
    private ApartmentDto apartment;
}
