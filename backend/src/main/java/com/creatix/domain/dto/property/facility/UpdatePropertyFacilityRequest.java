package com.creatix.domain.dto.property.facility;

import com.creatix.domain.enums.FacilityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class UpdatePropertyFacilityRequest {

    @ApiModelProperty(value = "Type of facility", required = true)
    private FacilityType type;
    @ApiModelProperty(value = "Facility info", required = true)
    private List<Detail> details;

    @ApiModel
    @Data
    public static class Detail {
        @ApiModelProperty(required = true)
        private String name;
        @ApiModelProperty(required = true)
        private String value;
        @ApiModelProperty(required = true)
        private Integer ordinal;
    }

}
