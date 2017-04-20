package com.creatix.domain.dto.business;

import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.entity.store.photo.BusinessProfileCartePhoto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by kvimbi on 19/04/2017.
 */
@ApiModel
@Data
public class BusinessProfileCarteItemDto {

    @ApiModelProperty
    private Long id;

    @ApiModelProperty(required = true)
    private String name;

    @ApiModelProperty(required = true)
    private double price;

    @ApiModelProperty
    BusinessProfileCartePhoto businessProfileCartePhoto;

    @ApiModelProperty(required = true)
    BusinessProfile businessProfile;

    @ApiModelProperty(value = "Photo file name", required = true)
    private String fileName;

    @ApiModelProperty(value = "Photo URL", required = true)
    private String fileUrl;

}
