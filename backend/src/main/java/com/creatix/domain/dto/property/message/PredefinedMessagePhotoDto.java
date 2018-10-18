package com.creatix.domain.dto.property.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for PredefinedMessagePhoto.
 * @author <a href="mailto:martin@thinkcreatix.com">martin dupal</a>
 */
@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class PredefinedMessagePhotoDto {
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "Photo file name", required = true)
    private String fileName;
    @ApiModelProperty(value = "Photo URL", required = true)
    private String fileUrl;
}
