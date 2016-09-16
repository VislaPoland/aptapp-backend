package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class AskResetPasswordRequest {
    @ApiModelProperty(required = true, value = "Primary email")
    @NotNull
    @Email
    private String email;

    public void setEmail(String email) {
        this.email = StringUtils.lowerCase(email);
    }
}
