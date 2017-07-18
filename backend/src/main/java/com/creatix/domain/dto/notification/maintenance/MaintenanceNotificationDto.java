package com.creatix.domain.dto.notification.maintenance;

import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.property.slot.MaintenanceReservationDto;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class MaintenanceNotificationDto extends NotificationDto {

    @ApiModelProperty(value = "Deprecated! Expect this to be null. <br />Target apartment")
    @Deprecated
    private BasicApartmentDto targetApartment;

    @ApiModelProperty(value = "Target apartment accessibility if tenant is not at home")
    private Boolean accessIfNotAtHome;

    @JsonView(Views.NotificationsWithReservation.class)
    @ApiModelProperty(value = "Maintenance reservations")
    private List<MaintenanceReservationDto> reservations;

}
