package com.creatix.domain.dto.property.slot;

import java.time.LocalTime;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
@Data
public class DayDuration {
    @NotNull
    @ApiModelProperty(value = "Time when slot starts", required = true, dataType = "java.lang.String", example = "09:00:00.000")
    private LocalTime beginTime;
    @NotNull
    @ApiModelProperty(value = "Time when slot ends", required = true, dataType = "java.lang.String", example = "17:00:00.000")
    private LocalTime endTime;
}
