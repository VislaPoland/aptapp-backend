package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Tomas Michalek on 19/04/2017.
 */
@ApiModel
@Data
public class BusinessProfileCarteItemDto {

    @ApiModelProperty
    private Long id;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String name;

    @Min(0)
    @NotNull
    @ApiModelProperty(required = true)
    private double price;

    @ApiModelProperty(value = "Photo URL", required = false)
    private String fileUrl;

}
