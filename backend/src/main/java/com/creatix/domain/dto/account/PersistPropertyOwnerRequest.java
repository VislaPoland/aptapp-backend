package com.creatix.domain.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Account data transfer object
 */
@ApiModel
@Getter
@Setter
public class PersistPropertyOwnerRequest extends PersistAccountRequest {

    @ApiModelProperty(value = "Web url")
    private String website;

}
