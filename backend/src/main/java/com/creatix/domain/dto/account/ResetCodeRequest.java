package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class ResetCodeRequest {
    @ApiModelProperty(value = "Account ID", required = true)
    @NotNull
    private Long accountId;
}
