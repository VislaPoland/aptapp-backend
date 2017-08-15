package com.creatix.domain.dto.notification.personal;

import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.notification.message.PersonalMessageDto;
import com.creatix.domain.enums.NeighborhoodNotificationResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class PersonalMessageNotificationDto extends NotificationDto {

    @ApiModelProperty(value = "Personal message", required = true)
    private PersonalMessageDto personalMessage;


}
