package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class UpdateAccountProfileRequest {
    @ApiModelProperty(value = "Secondary email address")
    private String secondaryEmail;

    @ApiModelProperty(value = "Secondary phone number")
    private String secondaryPhone;

    @ApiModelProperty(value = "Indicate that sms message notifications are enabled/disabled")
    private Boolean enableSms;
}
