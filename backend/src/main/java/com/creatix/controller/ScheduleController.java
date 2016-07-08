package com.creatix.controller;


import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.property.slot.*;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.MaintenanceReservationService;
import com.creatix.service.SlotService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping("/api/properties/{propertyId}/maintenance")
public class ScheduleController {
    @Autowired
    private SlotService slotService;
    @Autowired
    private MaintenanceReservationService maintenanceReservationService;
    @Autowired
    private Mapper mapper;

    @ApiOperation(value = "Get maintenance slots")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/slots", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<MaintenanceSlotDto>> getMaintenanceSlots(
            @PathVariable Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime beginDt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDt) {
        return new DataResponse<>(slotService.getMaintenanceSlotsByPropertyAndDateRange(propertyId, beginDt, endDt)
                .stream()
                .map(s -> mapper.toMaintenanceSlotDto(s))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Create maintenance reservation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/reservations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(AccountRole.Maintenance)
    public DataResponse<MaintenanceReservationDto> createMaintenanceReservation(
            @PathVariable Long propertyId,
            @RequestBody @Valid PersistMaintenanceReservationRequest request) {
        return new DataResponse<>(mapper.toMaintenanceReservationDto(maintenanceReservationService.createMaintenanceReservation(propertyId, request)));
    }

    @ApiOperation(value = "Delete maintenance reservation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/reservations/{reservationId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(AccountRole.Maintenance)
    public DataResponse<MaintenanceReservationDto> deleteMaintenanceReservation(@PathVariable Long reservationId) {
        return new DataResponse<>(mapper.toMaintenanceReservationDto(maintenanceReservationService.deleteById(reservationId)));
    }


    @ApiOperation(value = "Create maintenance slot schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/schedule", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<MaintenanceSlotScheduleDto> createMaintenanceSlotSchedule(
            @PathVariable Long propertyId,
            @RequestBody @Valid PersistMaintenanceSlotScheduleRequest request) {
        return new DataResponse<>(mapper.toMaintenanceSlotScheduleDto(slotService.createSchedule(propertyId, request)));
    }

    @ApiOperation(value = "Delete maintenance slot schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/schedule/{scheduleId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<MaintenanceSlotScheduleDto> deleteMaintenanceSlotSchedule(@PathVariable Long scheduleId) {
        return new DataResponse<>(mapper.toMaintenanceSlotScheduleDto(slotService.deleteScheduleById(scheduleId)));
    }
}
