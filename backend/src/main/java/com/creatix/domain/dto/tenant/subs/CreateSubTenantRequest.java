package com.creatix.domain.dto.tenant.subs;

import com.creatix.validator.UniqueEntityIdentifier;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel
@Data
public class CreateSubTenantRequest {

    @ApiModelProperty(value = "First name", required = true)
    @NotEmpty
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    @NotEmpty
    private String lastName;

    @ApiModelProperty(value = "Phone number", required = true)
    @NotNull
    @Pattern(regexp="^$|^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "The provided phone number is not valid")
    private String primaryPhone;

    @ApiModelProperty(value = "Email address", required = true)
    @NotEmpty
    @Email
    @UniqueEntityIdentifier
    private String primaryEmail;
}
