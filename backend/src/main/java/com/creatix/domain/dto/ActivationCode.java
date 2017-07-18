package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@ApiModel("Authentication code request object")
@Data
public class ActivationCode {

    @NotBlank
    @ApiModelProperty(value = "Authentication code", required = true)
    private String code;
}
