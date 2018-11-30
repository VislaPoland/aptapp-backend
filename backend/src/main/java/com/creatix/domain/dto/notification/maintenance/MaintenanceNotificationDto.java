package com.creatix.domain.dto.notification.maintenance;

import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.property.BasicAccountDto;
import com.creatix.domain.dto.property.slot.MaintenanceReservationDto;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class MaintenanceNotificationDto extends NotificationDto {

    @ApiModelProperty(value = "Target apartment - optional")
    private BasicApartmentDto targetApartment;

    @ApiModelProperty(value = "Target apartment accessibility if tenant is not at home")
    private Boolean accessIfNotAtHome;

    @ApiModelProperty(value = "Timestamp of response", dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime respondedAt;

    @ApiModelProperty(value = "Timestamp when notification was closed either as resolved or failed", dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime closedAt;

    @JsonView(Views.NotificationsWithReservation.class)
    @ApiModelProperty(value = "Maintenance reservations")
    private List<MaintenanceReservationDto> reservations;

    @ApiModelProperty(value = "Indicate if there is a pet present in apartment")
    private Boolean hasPet;

    @Size(max = 2048)
    @ApiModelProperty(value = "Pet instructions")
    private String petInstructions;

    @ApiModelProperty(value = "Last update by account")
    private BasicAccountDto updatedByAccount;
}
