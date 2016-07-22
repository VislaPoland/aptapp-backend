package com.creatix.domain.dto.notification.neighborhood;

import com.creatix.domain.enums.NeighborhoodNotificationResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class NeighborhoodNotificationResponseRequest {
    @ApiModelProperty(value = "Response", required = true)
    @NotNull
    private NeighborhoodNotificationResponse response;
}
