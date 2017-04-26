package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@ApiModel("Business profile")
@Data
@Accessors(chain = true)
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
    private List<BusinessCategoryDto> businessCategoryList;
    @ApiModelProperty("List of photos for business profile")
    private List<BusinessProfilePhotoDto> businessProfilePhotoList;
    @ApiModelProperty("Id of photo set as default image")
    private Long defaultPhotoId;
    @ApiModelProperty("Contact information")
    private BusinessContactDto contact;
    @ApiModelProperty(value = "Description", required = true)
    private String description;

}
