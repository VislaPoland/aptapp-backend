package com.creatix.domain.dto.notification.neighborhood;

import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.enums.NeighborhoodNotificationResponse;
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
public class NeighborhoodNotificationDto extends NotificationDto {
    @ApiModelProperty(value = "Target apartment", required = true)
    private BasicApartmentDto targetApartment;

    @ApiModelProperty(value = "Neighborhood notification response message")
    private NeighborhoodNotificationResponse response;

    @ApiModelProperty(value = "Timestamp of response", dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime respondedAt;
}
