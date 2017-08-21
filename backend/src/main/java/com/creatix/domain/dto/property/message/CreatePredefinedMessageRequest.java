package com.creatix.domain.dto.property.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class CreatePredefinedMessageRequest {
    @ApiModelProperty(value = "Message body", required = true)
    @NotEmpty
    @Size(max = 255)
    private String body;
}
