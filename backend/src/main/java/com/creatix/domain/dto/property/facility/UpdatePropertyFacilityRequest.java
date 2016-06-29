package com.creatix.domain.dto.property.facility;

import com.creatix.domain.enums.FacilityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class UpdatePropertyFacilityRequest {

    @ApiModelProperty(value = "Type of facility", required = true)
    private FacilityType type;
    @ApiModelProperty(value = "Facility name")
    private String name;
    @ApiModelProperty(value = "Facility description")
    private String description;
    @ApiModelProperty(value = "Facility opening hours")
    private String openingHours;
    @ApiModelProperty(value = "Facility location name")
    private String location;

}
