package com.creatix.domain.dto.notification;

import com.creatix.domain.dto.property.BasicAccountDto;
import com.creatix.domain.enums.NotificationHistoryStatus;
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
public class NotificationHistoryDto {
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "Author of the notification")
    private BasicAccountDto author;
    @ApiModelProperty(value = "Timestamp of notification history creation", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime createdAt;
    @ApiModelProperty(value = "Notification history status", required = true)
    private NotificationHistoryStatus status;
}
