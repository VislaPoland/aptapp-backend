package com.creatix.domain.dto.property;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.enums.PropertyStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class UpdatePropertyRequest {

    @NotEmpty
    @ApiModelProperty(value = "Name of property", required = true)
    private String name;

    @ApiModelProperty(value = "Additional information")
    private String additionalInformation;

    @NotNull
    @ApiModelProperty(value = "Additional information", required = true)
    private AddressDto address;

    @NotNull
    @ApiModelProperty(value = "ID of the property owner", required = true)
    private Long propertyOwnerId;

    @NotNull
    @ApiModelProperty(value = "Status of the property", required = true, notes = "Valid statuses are: Draft, Active")
    private PropertyStatus status;

}
