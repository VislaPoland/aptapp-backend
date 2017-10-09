package com.creatix.domain.dto.notification.security;

import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.enums.SecurityNotificationResponseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class SecurityNotificationDto extends NotificationDto {
    @ApiModelProperty(value = "Security notification response message")
    private SecurityNotificationResponseType response;
    @ApiModelProperty(value = "Timestamp of response", dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime respondedAt;
}
