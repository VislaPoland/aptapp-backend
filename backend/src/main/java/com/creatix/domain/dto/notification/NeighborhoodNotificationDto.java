package com.creatix.domain.dto.notification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class NeighborhoodNotificationDto {
    @ApiModelProperty(value = "Target apartment ID", required = true)
    private Long apartmentId;
}
