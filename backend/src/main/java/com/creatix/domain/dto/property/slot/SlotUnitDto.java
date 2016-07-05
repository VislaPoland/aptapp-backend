package com.creatix.domain.dto.property.slot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApiModel
@Data
public class SlotUnitDto {
    @ApiModelProperty(value = "Slot unit ID", required = true)
    private long id;
    @ApiModelProperty(value = "Slot ID", required = true)
    private long slotId;
    @ApiModelProperty(value = "Begin time of the slot unit", required = true)
    private Date beginTime;
    @ApiModelProperty(value = "Start time of the slot unit", required = true)
    private Date endTime;
    @ApiModelProperty(value = "Current capacity (1 = 1 person capacity)", required = true)
    private int capacity;
    @ApiModelProperty(value = "Initial capacity", required = true)
    private int initialCapacity;
    @ApiModelProperty(value = "Unit reservations")
    private List<MaintenanceReservationDto> reservations;

    @Transient
    @JsonIgnore
    public void addReservation(MaintenanceReservationDto reservation) {
        if ( reservations == null ) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
    }
}
