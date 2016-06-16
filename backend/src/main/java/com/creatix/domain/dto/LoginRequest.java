package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("Login request object")
public class LoginRequest {
    @ApiModelProperty(value = "Primary email address", required = true, example = "joe.tenant2@mail.com")
    @NotNull
    private String email;

    @ApiModelProperty(value = "Password", required = true, example = "password")
    @NotNull
    private String password;
}
