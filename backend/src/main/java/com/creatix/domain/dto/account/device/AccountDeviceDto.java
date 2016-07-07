package com.creatix.domain.dto.account.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Account device data transfer object
 */
@ApiModel
@Data
public class AccountDeviceDto {

    @ApiModelProperty(value = "Assigned push token", required = true)
    private String pushToken;

}
