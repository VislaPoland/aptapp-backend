package com.creatix.domain.dto.property.contact;

import com.creatix.domain.enums.CommunicationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class UpdatePropertyContactRequest {

    @NotNull
    @ApiModelProperty(value = "Type of contact", required = true)
    private String type;

    @NotNull
    @ApiModelProperty(required = true)
    private String value;

    @NotNull
    @ApiModelProperty(value = "Type of communication", required = true)
    private CommunicationType communicationType;

    /*
    @NotNull
    @ApiModelProperty(value = "ID of the property", required = true)
    private Long propertyId;
    */

}
