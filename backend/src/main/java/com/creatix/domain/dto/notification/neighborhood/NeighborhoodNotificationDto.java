package com.creatix.domain.dto.notification.neighborhood;

import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.notification.NotificationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class NeighborhoodNotificationDto extends NotificationDto {
    @ApiModelProperty(value = "Target apartment", required = true)
    private BasicApartmentDto targetApartment;
}
