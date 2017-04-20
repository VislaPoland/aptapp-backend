package com.creatix.domain.dto;

import com.creatix.domain.entity.store.photo.StoredPhotoType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by kvimbi on 19/04/2017.
 */
@ApiModel
@Data
public class GenericPhotoDto {

    @ApiModelProperty(required = true)
    private Long id;

    @ApiModelProperty(required = true)
    private String fileName;

    @ApiModelProperty(value = "Photo URL", required = true)
    private String fileUrl;

    @ApiModelProperty(required = true)
    private StoredPhotoType storedPhotoType;

}
