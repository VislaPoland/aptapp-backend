package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
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
    @Size(max = 120)
    @NotEmpty
    private String name;

    @ApiModelProperty("Business profile website")
    private String website;

    @ApiModelProperty("Latitude")
    private Double lat;

    @ApiModelProperty("Longitude")
    private Double lng;

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
    @Size(max = 2048)
    @NotEmpty
    private String description;

    @ApiModelProperty("Id of property business profile belongs to")
    private Long propertyId;

}
