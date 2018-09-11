package com.creatix.domain.dto.property;

import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class BasicAccountDto {

    @ApiModelProperty(required = true)
    private Long id;
    @ApiModelProperty(required = true)
    private AccountRole role;
    @ApiModelProperty(required = true)
    private String firstName;
    @ApiModelProperty(required = true)
    private String lastName;
    @ApiModelProperty(value = "Email address", required = true)
    private String primaryEmail;
    @ApiModelProperty(value = "Phone number")
    private String primaryPhone;
    @ApiModelProperty(value = "Unit number")
    private String unitNumber;

    @Deprecated
    @ApiModelProperty(required = true, notes = "*Deprecated*, use first and last name fields instead!")
    private String fullName;
}
