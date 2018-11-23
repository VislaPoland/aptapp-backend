package com.creatix.domain.dto.account;

import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

@ApiModel
@Data
public class UpdateAccountProfileRequest {

    @ApiModelProperty(value = "First name")
    private String firstName;
    @ApiModelProperty(value = "Last name")
    private String lastName;
    @Pattern(regexp="^$|^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "The provided phone number is not valid")
    @ApiModelProperty(value = "Primary phone")
    private String primaryPhone;
    @ApiModelProperty(value = "Web url")
    private String website;
    @Email
    @ApiModelProperty(value = "Secondary email address")
    private String secondaryEmail;
    @Pattern(regexp="^$|^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "The provided phone number is not valid")
    @ApiModelProperty(value = "Secondary phone number")
    private String secondaryPhone;
    @ApiModelProperty(value = "Indicate that sms message notifications are enabled/disabled")
    private Boolean enableSms;
    @ApiModelProperty
    private Boolean isTacAccepted;
    @ApiModelProperty
    private Boolean isPrivacyPolicyAccepted;
    @ApiModelProperty
    private Boolean isNeighborhoodNotificationEnable;
}
