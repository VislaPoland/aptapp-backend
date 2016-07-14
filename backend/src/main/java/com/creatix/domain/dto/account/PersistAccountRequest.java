package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
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

    @NotEmpty
    @ApiModelProperty(value = "Phone number", required = true)
    private String primaryPhone;

    @NotEmpty
    @ApiModelProperty(value = "Company name", required = true)
    private String companyName;

    @Email
    @ApiModelProperty(value = "Secondary email address")
    private String secondaryEmail;

    @ApiModelProperty(value = "Secondary phone number")
    private String secondaryPhone;


}
