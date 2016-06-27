package com.creatix.domain.dto.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class TenantSelfUpdateRequest {
    @NotNull
    @ApiModelProperty(required = true, value = "Password")
    private String password;
}
