package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class ResetPasswordRequest {
    @ApiModelProperty(value = "Reset password token", required = true)
    @NotNull
    private String token;

    @ApiModelProperty(value = "New password", required = true)
    @NotNull
    private String newPassword;
}
