package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("Authentication code request object")
@Data
public class VerificationCode {
    @ApiModelProperty(value = "Authentication code", required = true)
    private String code;
}
