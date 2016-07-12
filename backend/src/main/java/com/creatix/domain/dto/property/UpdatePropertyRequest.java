package com.creatix.domain.dto.property;

import com.creatix.domain.enums.PropertyStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel
@Getter
@Setter
public class UpdatePropertyRequest extends CreatePropertyRequest {

    @NotNull
    @ApiModelProperty(value = "Status of the property", required = true, notes = "Valid statuses are: Draft, Active")
    private PropertyStatus status;
}
