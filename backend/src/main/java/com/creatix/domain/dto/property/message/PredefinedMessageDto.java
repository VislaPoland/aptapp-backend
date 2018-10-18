package com.creatix.domain.dto.property.message;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel
@Data
public class PredefinedMessageDto {
    @ApiModelProperty(value = "id", required = true)
    @NotNull
    private Long id;
    @ApiModelProperty(value = "Message body", required = true)
    @NotEmpty
    @Size(max = 255)
    private String body;
    @ApiModelProperty(value = "Predefined Message photo")
    private List<PredefinedMessagePhotoDto> photos;
}
