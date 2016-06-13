package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("Successful login response")
@Data
public class LoginResponse {
    @ApiModelProperty(value = "Json web token (JWT)", required = true)
    private String token;
    @ApiModelProperty(value = "Authenticated account id", required = true)
    private long id;
    @ApiModelProperty(value = "Authenticated user of APT. app", required = true)
    private Object auth;
}
