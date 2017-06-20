package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("Login request object")
public class LoginRequest {

    @Email
    @NotNull
    @ApiModelProperty(value = "Primary email address", required = true, example = "joe.tenant2@mail.com")
    private String email;

    @NotBlank
    @ApiModelProperty(value = "Password", required = true, example = "password")
    private String password;

    public void setEmail(String email) {
        this.email = StringUtils.lowerCase(email);
    }
}
