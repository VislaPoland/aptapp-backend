package com.creatix.domain.dto.property;

import com.creatix.domain.dto.AddressDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel
@Data
public class CreatePropertyRequest {

    @NotEmpty
    @Size(max = 255)
    @ApiModelProperty(value = "Name of property", required = true)
    private String name;

    @NotNull
    @ApiModelProperty(value = "Property address", required = true)
    private AddressDto address;

    @NotNull
    @ApiModelProperty(value = "ID of the property owner", required = true)
    private Long propertyOwnerId;

    @NotNull
    @ApiModelProperty(value = "Time zone of the property")
    @Size(max = 255)
    private String timeZone;

    @ApiModelProperty(value = "Pay rent page url")
    @Size(max = 512)
    private String payRentUrl;

    @NotNull
    @ApiModelProperty(value = "Enable/disable sms notifications", required = true, notes = "Indicate that sms message notifications are enabled/disabled")
    private Boolean enableSms;
}
