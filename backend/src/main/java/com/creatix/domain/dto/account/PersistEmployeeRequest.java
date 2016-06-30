package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel
@Getter
@Setter
public class PersistEmployeeRequest extends PersistAccountRequest {

    @NotNull
    @ApiModelProperty(value = "Property manager id", required = true)
    private Long managerId;

}
