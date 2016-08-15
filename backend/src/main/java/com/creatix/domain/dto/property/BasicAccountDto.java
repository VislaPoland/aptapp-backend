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
    private String id;
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
    @ApiModelProperty
    private String companyName;
    @ApiModelProperty
    private Boolean deleted;


    @Deprecated
    @ApiModelProperty(notes = "deprecated, use primaryPhone instead")
    private String phone;
    @Deprecated
    @ApiModelProperty(notes = "deprecated, use primaryEmail instead")
    private String email;
    @Deprecated
    @ApiModelProperty(required = true)
    private String fullName;
}
