package com.creatix.domain.dto.tenant;

import com.creatix.domain.enums.TenantType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class UpdateTenantRequest {
    @NotBlank
    @ApiModelProperty(required = true, value = "First name")
    private String firstName;

    @NotBlank
    @ApiModelProperty(required = true, value = "Last name")
    private String lastName;

    @NotBlank
    @ApiModelProperty(required = true, value = "Company name")
    private String companyName;

    @NotBlank
    @ApiModelProperty(required = true, value = "Primary phone")
    private String primaryPhone;

    @NotBlank
    @Email
    @ApiModelProperty(required = true, value = "Primary email")
    private String primaryEmail;

    @NotNull
    @ApiModelProperty(required = true, value = "Tenant type")
    private TenantType type;

    @NotNull
    @ApiModelProperty(required = true, value = "Apartment ID")
    private Long apartmentId;
}
