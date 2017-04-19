package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@ApiModel
@Data
public class BusinessSearchRequest {

    @ApiModelProperty(value = "Restrict search within category", required = false)
    private Long businessCategoryId;
    @ApiModelProperty(value = "Search name", required = true)
    @NotNull
    private String name;

}
