package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.property.CreatePropertyRequest;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.dto.property.UpdatePropertyRequest;
import com.creatix.domain.entity.store.PropertyPhoto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.apartment.ApartmentService;
import com.creatix.service.property.PropertyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
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
    private ApartmentService apartmentService;

    @ApiOperation(value = "Get all properties")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<List<PropertyDetailsDto>> getAllProperties() {
        return new DataResponse<>(propertyService.getAllProperties().stream()
                .map(p -> mapper.toPropertyDetailsDto(p))
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
    public DataResponse<PropertyDetailsDto> getPropertyDetails(@PathVariable long propertyId) {
        return new DataResponse<>(mapper.toPropertyDetailsDto(propertyService.getProperty(propertyId)));
    }

    @ApiOperation(value = "Create new property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator})
    public DataResponse<PropertyDetailsDto> createProperty(@RequestBody @Valid CreatePropertyRequest request) {
        return new DataResponse<>(mapper.toPropertyDetailsDto(propertyService.createFromRequest(request)));
    }

    @ApiOperation(value = "Update property", notes = "Update existing property. This endpoint can only be called by account with Administrator role.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<PropertyDetailsDto> updateProperty(@PathVariable Long propertyId, @RequestBody @Valid UpdatePropertyRequest request) {
        return new DataResponse<>(mapper.toPropertyDetailsDto(propertyService.updateFromRequest(propertyId, request)));
    }

    @ApiOperation(value = "Delete property", notes = "Delete existing property.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator})
    public DataResponse<PropertyDetailsDto> deleteProperty(@PathVariable Long propertyId) {
        return new DataResponse<>(mapper.toPropertyDetailsDto(propertyService.deleteProperty(propertyId)));
    }

    @ApiOperation(value = "Get apartments")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{propertyId}/apartments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Administrator})
    public DataResponse<List<ApartmentDto>> getApartments(@PathVariable Long propertyId) {
        return new DataResponse<>(apartmentService.getApartmentsByPropertyId(propertyId).stream()
                .map(a -> mapper.toApartmentDto(a)).collect(Collectors.toList()));
    }

    @ApiOperation(value = "Upload property photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/{propertyId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PropertyDetailsDto> storeNotificationPhotos(@RequestParam MultipartFile[] files, @PathVariable long propertyId) throws IOException {
        return new DataResponse<>(mapper.toPropertyDetailsDto(propertyService.storePropertyPhotos(files, propertyId)));
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
}
