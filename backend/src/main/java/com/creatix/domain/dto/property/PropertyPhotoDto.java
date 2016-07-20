package com.creatix.domain.dto.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class PropertyPhotoDto {
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "Photo file name", required = true)
    private String fileName;
    @ApiModelProperty(value = "Photo URL", required = true)
    private String fileUrl;

}
