package com.creatix.domain.dto.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class RespondToRescheduleRequest {

    @ApiModel
    public enum RescheduleResponseType { Accept, Reject }

    @NotNull
    @ApiModelProperty(value = "Reschedule response type", required = true)
    private RescheduleResponseType responseType;
}
