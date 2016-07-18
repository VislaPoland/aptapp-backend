package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class PersistAssistantPropertyManagerRequest extends PersistEmployeeRequest {
    @ApiModelProperty(value = "Manager id")
    private Long managerId;
}
