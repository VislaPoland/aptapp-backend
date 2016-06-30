package com.creatix.domain.dto.notification.security;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@ApiModel
@Data
public class CreateSecurityNotificationRequest {
    @ApiModelProperty(value = "Short notification title", required = true, notes = "Maximum size of 20 letters")
    @NotNull
    @Size(max = 20)
    private String title;
    @ApiModelProperty(value = "Longer notification title", notes = "Maximum size of 100 letters")
    @Size(max = 100)
    private String description;
    @ApiModelProperty(value = "Timestamp of notification")
    private Date date = new Date();
}
