package com.creatix.domain.dto.property;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.enums.PropertyStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class CreatePropertyRequest {

    @NotNull
    @ApiModelProperty(value = "Name of property", required = true)
    private String name;

    @NotNull
    @ApiModelProperty(value = "Additional information")
    private String additionalInformation;

    @NotNull
    @ApiModelProperty(value = "Additional information")
    private AddressDto address;

    @NotNull
    @ApiModelProperty(value = "ID of the property owner", required = true)
    private Long propertyOwnerId;



}
