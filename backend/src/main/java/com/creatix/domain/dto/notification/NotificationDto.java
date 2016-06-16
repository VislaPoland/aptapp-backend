package com.creatix.domain.dto.notification;

import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("Notification object DTO")
@Data
public class NotificationDto {
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "Short notification description (20)", required = true)
    private String description;
    @ApiModelProperty(value = "Longer notification message (100)")
    private String message;
    @ApiModelProperty(value = "Notification status", required = true)
    private NotificationStatus status;
    @ApiModelProperty(value = "Timestamp of notification", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    @ApiModelProperty(value = "Response message")
    private String response;
    @ApiModelProperty(value = "Notification type", required = true)
    private NotificationType type;
}
