package com.creatix.domain.dto.notification.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by kvimbi on 29/05/2017.
 */
@Data
@ApiModel("From/To Account of personal message")
public class PersonalMessageAccountDto {

    @ApiModelProperty("User ID")
    private Long userId;
    @ApiModelProperty("User's first name")
    private String firstName;
    @ApiModelProperty("User's last name")
    private String lastName;
    @ApiModelProperty("Company's name")
    private String companyName;
    @ApiModelProperty("User's primary phone")
    private String primaryPhone;
    @ApiModelProperty("User's primary email")
    private String primaryEmail;

}
