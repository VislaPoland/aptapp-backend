package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AudienceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@ApiModel
@Getter
@Setter
public class UpdateEventSlotRequest {

    @NotEmpty
    @ApiModelProperty(value = "Title", required = true)
    private String title;
    @ApiModelProperty(value = "Description")
    private String description;
    @ApiModelProperty(value = "Location")
    private String location;
    @ApiModelProperty(value = "Audience", required = true)
    private AudienceType audience;
}
