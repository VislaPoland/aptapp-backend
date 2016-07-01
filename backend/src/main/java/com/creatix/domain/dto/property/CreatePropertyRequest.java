package com.creatix.domain.dto.property;

import com.creatix.domain.dto.AddressDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class CreatePropertyRequest {

    @NotEmpty
    @ApiModelProperty(value = "Name of property", required = true)
    private String name;

    @NotNull
    @ApiModelProperty(value = "Property address", required = true)
    private AddressDto address;

    @NotNull
    @ApiModelProperty(value = "ID of the property owner", required = true)
    private Long propertyOwnerId;

    @ApiModelProperty(value = "Time zone of the property")
    private String timeZone;
}
