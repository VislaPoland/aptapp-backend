package com.creatix.domain.dto.notification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel
@Data
public class NotificationsCollectionRequest {
    @ApiModelProperty(value = "From date", required = true)
    @NotNull
    private Date from;

    @ApiModelProperty(value = "Till date", required = true)
    @NotNull
    private Date till;
}
