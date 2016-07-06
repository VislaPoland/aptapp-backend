package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class CreatePasswordRequest {
    @NotNull
    @ApiModelProperty(required = true, value = "Password")
    private String password;
}
