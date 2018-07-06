package com.creatix.domain.dto.property.slot;

import com.creatix.domain.dto.AttachmentDto;
import com.creatix.domain.enums.AudienceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

@ApiModel
@Getter
@Setter
public class EventPhotoDto extends AttachmentDto {

}