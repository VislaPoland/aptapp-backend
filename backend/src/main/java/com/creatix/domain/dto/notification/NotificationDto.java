package com.creatix.domain.dto.notification;

import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class NotificationDto {
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "Short notification title", required = true)
    private String title;
    @ApiModelProperty(value = "Longer notification title")
    private String description;
    @ApiModelProperty(value = "Notification status", required = true)
    private NotificationStatus status;
    @ApiModelProperty(value = "Timestamp of notification creation", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime createdAt;
    @ApiModelProperty(value = "Timestamp of notification last modification", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime updatedAt;
    @ApiModelProperty(value = "Notification type", required = true)
    private NotificationType type;
    @ApiModelProperty(value = "Notification photo")
    private List<NotificationPhotoDto> photos;
}
