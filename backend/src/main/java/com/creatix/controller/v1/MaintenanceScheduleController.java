package com.creatix.controller.v1;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.property.slot.MaintenanceSlotScheduleDto;
import com.creatix.domain.dto.property.slot.PersistMaintenanceSlotScheduleRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ApplicationFeatureType;
import com.creatix.domain.mapper.PropertyMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.SlotService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Transactional
@RequestMapping(path = {"/api/properties/{propertyId}/schedule", "/api/v1/properties/{propertyId}/schedule"})
@ApiVersion(1.0)
public class MaintenanceScheduleController {
    @Autowired
    private SlotService slotService;
    @Autowired
    private PropertyMapper propertyMapper;

    @ApiOperation(value = "Create property schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.PropertyOwner}, feature = ApplicationFeatureType.MAINTENANCE)
    public DataResponse<MaintenanceSlotScheduleDto> createPropertySchedule(@PathVariable Long propertyId, @RequestBody @Valid PersistMaintenanceSlotScheduleRequest request) {
        return new DataResponse<>(propertyMapper.toMaintenanceSlotScheduleDto(slotService.createSchedule(propertyId, request)));
    }

    @ApiOperation(value = "Update property schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.PropertyOwner}, feature = ApplicationFeatureType.MAINTENANCE)
    public DataResponse<MaintenanceSlotScheduleDto> updatePropertySchedule(@PathVariable Long propertyId, @RequestBody @Valid PersistMaintenanceSlotScheduleRequest request) {
        return new DataResponse<>(propertyMapper.toMaintenanceSlotScheduleDto(slotService.createSchedule(propertyId, request)));
    }


}
