package com.creatix.domain.dto.notification;

import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("Maintenance notification object DTO")
@Data
public class MaintenanceNotificationDto {
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "Short notification title", required = true)
    private String title;
    @ApiModelProperty(value = "Longer notification description")
    private String description;
    @ApiModelProperty(value = "Notification status", required = true)
    private NotificationStatus status;
    @ApiModelProperty(value = "Timestamp of notification", required = true, example = "2016-06-1 00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    @ApiModelProperty(value = "Response message")
    private String response;
    @ApiModelProperty(value = "Notification type", required = true)
    private NotificationType type;
    @ApiModelProperty(value = "Target apartment ID", required = true)
    private Long apartmentId;
    @ApiModelProperty(value = "Target apartment accessibility if tenant is not at home", required = true)
    private Boolean accessIfNotAtHome;
}
