package com.creatix.domain.dto;

import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.enums.AccountRole;
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
    @ApiModelProperty(value = "Authenticated user role")
    private AccountRole role;
    @ApiModelProperty(value = "Authenticated user of APT. app", required = true)
    private AccountDto account;
}
