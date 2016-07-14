package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.property.CreatePropertyRequest;
import com.creatix.domain.dto.property.PropertyDto;
import com.creatix.domain.dto.property.UpdatePropertyRequest;
import com.creatix.domain.dto.property.slot.ScheduledSlotsResponse;
import com.creatix.domain.entity.store.PropertyPhoto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.SlotService;
import com.creatix.service.apartment.ApartmentService;
import com.creatix.service.property.PropertyService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping("/api/properties")
public class PropertyController {
    @Autowired
    private Mapper mapper;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private SlotService slotService;
    @Autowired
    private ApartmentService apartmentService;

    @ApiOperation(value = "Get all properties")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<List<PropertyDto>> getAllProperties() {
        return new DataResponse<>(propertyService.getAllProperties().stream()
                .map(p -> mapper.toPropertyDto(p))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get property detail")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<PropertyDto> getPropertyDetails(@PathVariable long propertyId) {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.getProperty(propertyId)));
    }

    @ApiOperation(value = "Create new property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator})
    public DataResponse<PropertyDto> createProperty(@RequestBody @Valid CreatePropertyRequest request) {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.createFromRequest(request)));
    }

    @ApiOperation(value = "Update property", notes = "Update existing property. This endpoint can only be called by account with Administrator role.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<PropertyDto> updateProperty(@PathVariable Long propertyId, @RequestBody @Valid UpdatePropertyRequest request) {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.updateFromRequest(propertyId, request)));
    }

    @ApiOperation(value = "Delete property", notes = "Delete existing property.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator})
    public DataResponse<PropertyDto> deleteProperty(@PathVariable Long propertyId) {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.deleteProperty(propertyId)));
    }

    @ApiOperation(value = "Upload property photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/{propertyId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PropertyDto> storeNotificationPhotos(@RequestParam MultipartFile[] files, @PathVariable long propertyId) throws IOException {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.storePropertyPhotos(files, propertyId)));
    }

    @ApiOperation(value = "Download property photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/{propertyId}/photos/{photoId}", method = RequestMethod.GET)
    @RoleSecured
    @ResponseBody
    public HttpEntity<byte[]> getFile(@PathVariable Long propertyId, @PathVariable Long photoId) throws IOException {
        final PropertyPhoto file = propertyService.getPropertyPhoto(photoId);
        final byte[] fileData = FileUtils.readFileToByteArray(new File(file.getFilePath()));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(fileData.length);

        return new HttpEntity<>(fileData, headers);
    }

    @ApiOperation(value = "Get scheduled events", notes = "Get all scheduled events for single property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{propertyId}/schedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<ScheduledSlotsResponse> getEvents(
            @PathVariable Long propertyId,
            @ApiParam(example = "2016-07-07") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginDt,
            @ApiParam(example = "2016-07-07") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDt,
            @ApiParam @RequestParam(required = false) Long startId,
            @ApiParam @RequestParam(required = false) Integer pageSize) {

        return new DataResponse<>(slotService.getSlotsByFilter(propertyId, beginDt, endDt, startId, pageSize));
    }
}
