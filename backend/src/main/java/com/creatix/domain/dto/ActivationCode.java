package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("Authentication code request object")
@Data
public class ActivationCode {
    @ApiModelProperty(value = "Authentication code", required = true)
    @NotNull
    private String code;
}
