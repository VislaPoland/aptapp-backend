package com.creatix.domain.dto.property.facility;

import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.enums.CommunicationType;
import com.creatix.domain.enums.ContactType;
import com.creatix.domain.enums.FacilityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel
@Data
public class CreatePropertyFacilityRequest {

    @ApiModelProperty(value = "Type of facility", required = true)
    private FacilityType type;
    //    @ApiModelProperty(value = "Facility info", required = true)
    @ApiModelProperty(value = "Facility info")
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
