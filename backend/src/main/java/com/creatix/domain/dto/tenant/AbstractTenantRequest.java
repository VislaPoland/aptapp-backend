package com.creatix.domain.dto.tenant;

import com.creatix.domain.enums.TenantType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
abstract class AbstractTenantRequest {
    @NotNull
    @ApiModelProperty(required = true)
    private Long apartmentId;
    @NotNull
    @ApiModelProperty(required = true)
    private TenantType type;
    @ApiModelProperty
    private String companyName;
    @ApiModelProperty
    private String firstName;
    @ApiModelProperty
    private String lastName;
    @ApiModelProperty
    private String primaryPhone;
    @ApiModelProperty
    private String primaryEmail;
}
