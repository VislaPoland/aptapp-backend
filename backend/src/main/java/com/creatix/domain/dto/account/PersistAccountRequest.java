package com.creatix.domain.dto.account;

import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Account data transfer object
 */
@ApiModel
@Getter
@Setter
public class PersistAccountRequest {

    @NotEmpty
    @ApiModelProperty(value = "First name", required = true)
    private String firstName;

    @NotEmpty
    @ApiModelProperty(value = "Last name", required = true)
    private String lastName;

    @Email
    @NotEmpty
    @ApiModelProperty(value = "Email address", required = true)
    private String primaryEmail;

    @Pattern(regexp="^$|^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "The provided phone number is not valid")
    @ApiModelProperty(value = "Phone number")
    private String primaryPhone;

    @Email
    @ApiModelProperty(value = "Secondary email address")
    private String secondaryEmail;

    @Pattern(regexp="^$|^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "The provided phone number is not valid")
    @ApiModelProperty(value = "Secondary phone number")
    private String secondaryPhone;

    @ApiModelProperty(value = "Is neighborhood notification enable?")
    private Boolean isNeighborhoodNotificationEnable;

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = StringUtils.lowerCase(primaryEmail);
    }
}
