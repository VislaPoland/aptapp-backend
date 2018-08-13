package com.creatix.domain.dto.notification.maintenance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @NotNull
    @ApiModelProperty(value = "ID of the selected slot unit", required = true)
    private Long slotUnitId;

    @ApiModelProperty(value = "unit number making request - optional")
    private String unitNumber;

    @ApiModelProperty(value = "Target apartment accessibility if tenant is not at home")
    private Boolean accessIfNotAtHome;

    @ApiModelProperty(value = "Indicate if there is a pet present in apartment")
    private Boolean hasPet;

    @Size(max = 2048)
    @ApiModelProperty(value = "Pet instructions")
    private String petInstructions;

    @Nullable
    @ApiModelProperty(value = "ID of the selected property unit", required = true)
    private Long propertyId;
}
