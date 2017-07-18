package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@ApiModel("Business contact information")
@Data
public class BusinessCategoryDto {

    @ApiModelProperty(value = "Category id", required = true, readOnly = true)
    private Long id;

    @ApiModelProperty(value = "Category name", required = true, readOnly = true)
    @NotBlank
    @Size(max = 255)
    private String name;

}
