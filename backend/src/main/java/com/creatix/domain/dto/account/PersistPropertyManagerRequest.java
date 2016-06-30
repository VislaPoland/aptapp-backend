package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Account data transfer object
 */
@ApiModel
@Getter
@Setter
public class PersistPropertyManagerRequest extends PersistAccountRequest {

    @NotNull
    @ApiModelProperty(value = "Managed property id", required = true)
    private Long managedPropertyId;

}
