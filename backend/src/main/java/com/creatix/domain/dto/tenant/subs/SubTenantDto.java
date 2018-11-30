package com.creatix.domain.dto.tenant.subs;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.property.PropertyDto;
import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@ApiModel
@Data
public class SubTenantDto {
    @ApiModelProperty(value = "Sub-tenant ID", required = true)
    private Long id;

    @ApiModelProperty(value = "First name", required = true)
    @NotEmpty
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    @NotEmpty
    private String lastName;

    @ApiModelProperty(value = "Full name", required = true)
    private String fullName;

    @ApiModelProperty(value = "Phone number", required = true)
    private String primaryPhone;

    @ApiModelProperty(value = "Email address", required = true)
    @NotEmpty
    private String primaryEmail;

    @ApiModelProperty(value = "Role", required = true)
    private AccountRole role;

    @ApiModelProperty(value = "Parent tenant ID")
    private Long parentTenantId;

    @ApiModelProperty(value = "Address", required = true)
    private AddressDto address;

    @ApiModelProperty(value = "Associated property details", required = true)
    private PropertyDto property;

    @ApiModelProperty(value = "Associated apartment", required = true)
    private ApartmentDto apartment;

    @ApiModelProperty
    private Boolean isTacAccepted;
    @ApiModelProperty
    private Boolean isPrivacyPolicyAccepted;
    @ApiModelProperty
    private Boolean isNeighborhoodNotificationEnable;
}