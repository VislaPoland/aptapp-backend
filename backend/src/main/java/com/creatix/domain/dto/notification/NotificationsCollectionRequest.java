package com.creatix.domain.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel
@Data
public class NotificationsCollectionRequest {
    @ApiModelProperty(value = "From date", required = true, example = "2016-06-1 00:00:00")
    @NotNull
    private Date from;

    @ApiModelProperty(value = "Till date", required = true, example = "2016-06-30 23:59:59")
    @NotNull
    private Date till;
}
