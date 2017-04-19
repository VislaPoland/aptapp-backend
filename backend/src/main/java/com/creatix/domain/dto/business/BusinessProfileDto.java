package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@ApiModel("Business profile")
@Data
public class BusinessProfileDto {

    @ApiModelProperty(value = "Id", required = true, readOnly = true)
    private Long id;
    @ApiModelProperty(value = "Business profile name", required = true)
    private String name;
    @ApiModelProperty("Latitude")
    private Long lat;
    @ApiModelProperty("Longitude")
    private Long lng;
    @ApiModelProperty(value = "Whether has business profile image uploaded or not", readOnly = true)
    private boolean isImageUploaded;
    @ApiModelProperty("List of categories business is included in")
    List<BusinessCategoryDto> businessCategoryList;
    @ApiModelProperty("Contact information")
    private BusinessContactDto contact;
    @ApiModelProperty(value = "Description", required = true)
    private String description;

}
