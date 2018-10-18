package com.creatix.domain.dto.account;

import javax.annotation.Nullable;

import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public abstract class PersistEmployeeRequest extends PersistAccountRequest {

    @Nullable
    @ApiModelProperty(value = "propertyId", required = false)
    private Long propertyId;
}
