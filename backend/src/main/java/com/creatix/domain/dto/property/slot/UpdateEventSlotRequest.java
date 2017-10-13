package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AudienceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.OffsetDateTime;

@ApiModel
@Getter
@Setter
public class UpdateEventSlotRequest {

    @NotEmpty
    @ApiModelProperty(value = "Title")
    private String title;
    @ApiModelProperty(value = "Description")
    private String description;
    @ApiModelProperty(value = "Location")
    private String location;
    @ApiModelProperty(value = "Audience")
    private AudienceType audience;

    @ApiModelProperty(value = "Time when slot starts", example = "2016-07-07T10:37:47.960Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(value = "Slot duration in minutes")
    private Integer unitDurationMinutes;
}
