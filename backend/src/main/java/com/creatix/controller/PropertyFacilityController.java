package com.creatix.controller;

import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.dto.property.contact.CreatePropertyContactRequest;
import com.creatix.domain.dto.property.contact.UpdatePropertyContactRequest;
import com.creatix.domain.dto.property.facility.CreatePropertyFacilityRequest;
import com.creatix.domain.dto.property.facility.UpdatePropertyFacilityRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.PropertyMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.property.PropertyFacilityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping(value = "/api/properties/{propertyId}/facilities")
public class PropertyFacilityController {

    @Autowired
    private PropertyMapper mapper;
    @Autowired
    private PropertyFacilityService propertyFacilityService;

    @ApiOperation(value = "Get all property facilities")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<PropertyDetailsDto.Facility>> getAllPropertyFacilities(@PathVariable Long propertyId) {
        return new DataResponse<>(propertyFacilityService.details(propertyId).stream()
                .map(f -> mapper.toPropertyFacility(f))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get property facility detail")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{facilityId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PropertyDetailsDto.Facility> getPropertyFacilty(@PathVariable Long propertyId, @PathVariable Long facilityId) {
        return new DataResponse<>(mapper.toPropertyFacility(propertyFacilityService.detail(propertyId, facilityId)));
    }

    @ApiOperation(value = "Create new property facility")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<PropertyDetailsDto.Facility> createPropertyFacility(@PathVariable Long propertyId, @Valid @RequestBody CreatePropertyFacilityRequest request) {
        return new DataResponse<>(mapper.toPropertyFacility(propertyFacilityService.create(propertyId, request)));
    }

    @ApiOperation(value = "Update property facility", notes = "Update existing property facility. This endpoint can only be called by account with Manager role.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{facilityId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<PropertyDetailsDto.Facility> updatePropertyFacility(@PathVariable Long propertyId, @PathVariable Long facilityId, @Valid @RequestBody UpdatePropertyFacilityRequest request) {
        return new DataResponse<>(mapper.toPropertyFacility(propertyFacilityService.update(propertyId, facilityId, request)));
    }

    @ApiOperation(value = "Delete property facility", notes = "Delete existing property facility.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{facilityId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<PropertyDetailsDto.Facility> deletePropertyFacility(@PathVariable Long propertyId, @PathVariable Long facilityId) {
        return new DataResponse<>(mapper.toPropertyFacility(propertyFacilityService.delete(propertyId, facilityId)));
    }

}
