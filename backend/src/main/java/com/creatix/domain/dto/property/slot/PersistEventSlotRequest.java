package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AudienceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@ApiModel
@Data
public class PersistEventSlotRequest {
    @ApiModelProperty(value = "Time when slot starts", required = true, example = "2016-07-07T10:37:47.960Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(value = "Slot duration in minutes", required = true)
    private int durationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 person capacity)", required = true)
    private int initialCapacity;
    @NotEmpty
    @ApiModelProperty(value = "Title", required = true)
    private String title;
    @ApiModelProperty(value = "Description")
    private String description;
    @ApiModelProperty(value = "Location")
    private String location;
    @NotNull
    @ApiModelProperty(value = "Audience", required = true)
    private AudienceType audience;
}
