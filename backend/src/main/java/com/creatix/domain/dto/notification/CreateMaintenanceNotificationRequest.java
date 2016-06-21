package com.creatix.domain.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@ApiModel
@Data
public class CreateMaintenanceNotificationRequest {
    @ApiModelProperty(value = "Short notification title", required = true, notes = "Maximum size of 20 letters")
    @NotNull
    @Size(max = 20)
    private String title;
    @ApiModelProperty(value = "Longer notification description", notes = "Maximum size of 100 letters")
    @Size(max = 100)
    private String description;
    @ApiModelProperty(value = "Timestamp of notification", required = true, example = "2016-06-1 00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date date;
    @ApiModelProperty(value = "Target apartment unit number", required = true)
    @NotNull
    private String unitNumber;
    @ApiModelProperty(value = "Target apartment accessibility if tenant is not at home", required = true)
    @NotNull
    private Boolean accessIfNotAtHome;
}
