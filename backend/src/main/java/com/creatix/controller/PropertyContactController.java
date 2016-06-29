package com.creatix.controller;

import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.dto.property.contact.CreatePropertyContactRequest;
import com.creatix.domain.dto.property.contact.UpdatePropertyContactRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.PropertyMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.property.PropertyContactService;
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
@RequestMapping(value = "/api/properties/{propertyId}/contacts")
public class PropertyContactController {

    @Autowired
    private PropertyMapper mapper;
    @Autowired
    private PropertyContactService propertyContactService;

    @ApiOperation(value = "Get all property contacts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<PropertyDetailsDto.Contact>> getAllPropertyContacts(@PathVariable Long propertyId) {
        return new DataResponse<>(propertyContactService.details(propertyId).stream()
                .map(p -> mapper.toPropertyContact(p))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get property contact detail")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{contactId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PropertyDetailsDto.Contact> getPropertyContact(@PathVariable Long propertyId, @PathVariable Long contactId) {
        return new DataResponse<>(mapper.toPropertyContact(propertyContactService.detail(propertyId, contactId)));
    }

    @ApiOperation(value = "Create new property contact")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<PropertyDetailsDto.Contact> createPropertyContact(@PathVariable Long propertyId, @Valid @RequestBody CreatePropertyContactRequest request) {
        return new DataResponse<>(mapper.toPropertyContact(propertyContactService.create(propertyId, request)));
    }

    @ApiOperation(value = "Update property contact", notes = "Update existing property contact. This endpoint can only be called by account with Manager role.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{contactId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<PropertyDetailsDto.Contact> updatePropertyContact(@PathVariable Long propertyId, @PathVariable Long contactId, @Valid @RequestBody UpdatePropertyContactRequest request) {
        return new DataResponse<>(mapper.toPropertyContact(propertyContactService.update(propertyId, contactId, request)));
    }

    @ApiOperation(value = "Delete property contact", notes = "Delete existing property contact.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{contactId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<PropertyDetailsDto.Contact> deletePropertyContact(@PathVariable Long propertyId, @PathVariable Long contactId) {
        return new DataResponse<>(mapper.toPropertyContact(propertyContactService.delete(propertyId, contactId)));
    }

}
