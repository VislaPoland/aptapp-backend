package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Account data transfer object
 */
@ApiModel("Account DTO")
@Data
public class AccountDto {
    @ApiModelProperty(value = "ID of the entity", required = true)
    private long id;
    @ApiModelProperty(value = "First name or given name", required = true)
    private String firstName;
    @ApiModelProperty(value = "Last name or surname", required = true)
    private String lastName;
    @ApiModelProperty(value = "Email address", required = true)
    private String email;
}
