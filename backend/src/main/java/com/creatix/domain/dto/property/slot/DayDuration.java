package com.creatix.domain.dto.property.slot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
@Data
@Accessors(chain = true)
public class DayDuration {
    @NotNull
    @ApiModelProperty(value = "Time when slot starts", required = true, dataType = "java.lang.String", example = "09:00:00.000")
    private LocalTime beginTime;
    @NotNull
    @ApiModelProperty(value = "Time when slot ends", required = true, dataType = "java.lang.String", example = "17:00:00.000")
    private LocalTime endTime;
}
