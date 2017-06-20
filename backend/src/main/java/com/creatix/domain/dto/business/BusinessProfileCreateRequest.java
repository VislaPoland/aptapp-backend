package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by kvimbi on 20/06/2017.
 */
@Data
public class BusinessProfileCreateRequest {

    @ApiModelProperty(value = "Business profile name", required = true)
    @Size(max = 255)
    @NotBlank
    private String name;
    @ApiModelProperty("Latitude")
    private Double lat;
    @ApiModelProperty("Longitude")
    private Double lng;
    @ApiModelProperty("List of categories business is included in")
    private List<BusinessCategoryDto> businessCategoryList;
    @ApiModelProperty("Contact information")
    private BusinessContactDto contact;
    @ApiModelProperty(value = "Description", required = true)
    @Size(max = 2048)
    @NotBlank
    private String description;
    @ApiModelProperty("Id of property business profile belongs to")
    private Long propertyId;

    @ApiModelProperty("Notification is sent to tenants if set to true")
    private boolean shouldSentNotification = false;

}
