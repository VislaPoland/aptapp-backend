package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class AskResetPasswordRequest {
    @ApiModelProperty(required = true, value = "Primary email")
    @NotNull
    @Email
    private String email;
}
