package com.creatix.domain.dto.tenant.subs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class PersistSubTenantRequest {

    @ApiModelProperty(value = "First name", required = true)
    @NotNull
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    @NotNull
    private String lastName;

    @ApiModelProperty(value = "Phone number", required = true)
    @NotNull
    private String phone;

    @ApiModelProperty(value = "Email address", required = true)
    @NotNull
    @Email
    private String email;
}
