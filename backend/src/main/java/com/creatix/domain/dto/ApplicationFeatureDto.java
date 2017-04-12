package com.creatix.domain.dto;

import com.creatix.domain.enums.ApplicationFeatureType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by kvimbi on 12/04/2017.
 */
@Data
@ApiModel
public class ApplicationFeatureDto {

    private Long id;
    private ApplicationFeatureType applicationFeatureType;
    private boolean enabled;

}
