package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class UpdatePasswordRequest {
    @NotNull
    @ApiModelProperty(required = true, value = "Old password")
    private String oldPassword;

    @NotNull
    @ApiModelProperty(required = true, value = "New password")
    private String newPassword;
}
