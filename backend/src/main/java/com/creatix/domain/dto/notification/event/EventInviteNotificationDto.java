package com.creatix.domain.dto.notification.event;

import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.property.slot.EventSlotDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class EventInviteNotificationDto extends NotificationDto {
    @ApiModelProperty(value = "Event")
    private EventSlotDto event;
}
