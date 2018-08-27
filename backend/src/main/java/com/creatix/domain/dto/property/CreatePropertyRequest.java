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

    @ApiModelProperty(value = "ID of the property owner")
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

    @ApiModelProperty(value = "Lock out hours, lockout after repetition msgs in hours, default set to 24h", required = true, notes = "Lockout after repetition msgs in hours")
    private Integer lockoutHours;

    @ApiModelProperty(value = "Throttle fast, time of locking msg after last one in minutes, default set to 15min", required = true, notes = "Time of locking msg after last one in minutes")
    private Integer throttleFastMinutes;

    @ApiModelProperty(value = "Throttle fast, time of locking msg after last one in minutes, default set to 15min", required = true, notes = "Time of locking msg after last one in minutes")
    private Integer throttleSlowLimit;

    @ApiModelProperty(value = "Enable sending of sms in case of several notifications type escalation", required = true)
    private Boolean enableSmsEscalation;

    @ApiModelProperty(value = "Enable sending of email in case of several notifications type escalation", required = true)
    private Boolean enableEmailEscalation;
}
