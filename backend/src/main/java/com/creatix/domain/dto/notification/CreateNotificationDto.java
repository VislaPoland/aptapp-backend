package com.creatix.domain.dto.notification;

import com.creatix.domain.enums.NotificationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel("Notification object DTO")
@Data
public class CreateNotificationDto {
    @ApiModelProperty(value = "Short notification title", required = true)
    @NotNull
    private String title;
    @ApiModelProperty(value = "Longer notification title")
    private String description;
    @ApiModelProperty(value = "Notification status", required = true)
    @NotNull
    private NotificationStatus status;
    @ApiModelProperty(value = "Timestamp of notification", required = true, example = "2016-06-1 00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Future
    private Date date;
    @ApiModelProperty(value = "Response message")
    private String response;
}
