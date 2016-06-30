package com.creatix.domain.dto.notification.maintenance;

import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
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
    @ApiModelProperty(value = "Timestamp of notification", required = true)
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
