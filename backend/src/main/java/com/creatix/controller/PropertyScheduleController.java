package com.creatix.controller;

import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.property.schedule.PropertyScheduleDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.PropertyMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.property.PropertyScheduleService;
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
@RequestMapping("/api/properties/{propertyId}/schedule")
public class PropertyScheduleController {
    @Autowired
    private PropertyScheduleService propertyScheduleService;
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
    @RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<PropertyScheduleDto> createPropertySchedule(@PathVariable Long propertyId, @RequestBody @Valid PropertyScheduleDto request) {
        return new DataResponse<>(propertyMapper.toPropertyScheduleDto(propertyScheduleService.createPropertyScheduleFromRequest(propertyId, request)));
    }

    @ApiOperation(value = "Update property schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(path = "", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<PropertyScheduleDto> updatePropertySchedule(@PathVariable Long propertyId, @RequestBody @Valid PropertyScheduleDto request) {
        return new DataResponse<>(propertyMapper.toPropertyScheduleDto(propertyScheduleService.updatePropertyScheduleFromRequest(propertyId, request)));
    }
}
