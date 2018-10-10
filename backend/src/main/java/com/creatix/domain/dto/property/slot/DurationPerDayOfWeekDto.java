package com.creatix.domain.dto.property.slot;

import java.time.LocalTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
@ApiModel
@Data
public class DurationPerDayOfWeekDto {

    @ApiModelProperty(value = "Time when slot starts", required = true, dataType = "java.lang.String", example = "09:00:00.000")
    private LocalTime beginTime;
    @ApiModelProperty(value = "Time when slot ends", required = true, dataType = "java.lang.String", example = "17:00:00.000")
    private LocalTime endTime;

    public DurationPerDayOfWeekDto(LocalTime beginTime, LocalTime endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}
