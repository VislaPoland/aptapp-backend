package com.creatix.domain.dto.notification;

import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class NotificationPhotoDto {
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "Photo file name", required = true)
    private String fileName;
}
