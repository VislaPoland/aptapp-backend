package com.creatix.domain.dto.notification.security;

import com.creatix.domain.enums.SecurityNotificationResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class SecurityNotificationResponseRequest {
    @ApiModelProperty(value = "Response", required = true)
    @NotNull
    private SecurityNotificationResponse response;
}
