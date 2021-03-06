package com.creatix.controller.v1;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.ErrorMessage;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.apartment.PersistApartmentRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.apartment.ApartmentService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping(path = {"/api/properties/{propertyId}", "/api/v1/properties/{propertyId}"})
@ApiVersion(1.0)
public class ApartmentController {

    @Autowired
    private Mapper mapper;
    @Autowired
    private ApartmentService apartmentService;

    @ApiOperation(value = "Get apartments")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/apartments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.Security, AccountRole.Maintenance})
    public DataResponse<List<ApartmentDto>> getApartments(@PathVariable Long propertyId) {
        return new DataResponse<>(apartmentService.getApartmentsByPropertyId(propertyId).stream()
                .map(a -> mapper.toApartmentDto(a)).collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get apartment details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/apartments/{apartmentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<ApartmentDto> getApartment(@PathVariable Long apartmentId) {
        return new DataResponse<>(mapper.toApartmentDto(apartmentService.getApartment(apartmentId)));
    }

    @ApiOperation(value = "Create apartment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/apartments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public DataResponse<ApartmentDto> createApartment(@PathVariable Long propertyId, @Valid @RequestBody PersistApartmentRequest request) {
        return new DataResponse<>(mapper.toApartmentDto(apartmentService.createApartment(propertyId, request)));
    }

    @ApiOperation(value = "Update apartment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/apartments/{apartmentId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public DataResponse<ApartmentDto> updateApartment(@PathVariable Long apartmentId, @Valid @RequestBody PersistApartmentRequest request) {
        return new DataResponse<>(mapper.toApartmentDto(apartmentService.updateApartment(apartmentId, request)));
    }

    @ApiOperation(value = "Delete apartment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/apartments/{apartmentId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public DataResponse<ApartmentDto> deleteApartment(@PathVariable Long apartmentId) {
        return new DataResponse<>(mapper.toApartmentDto(apartmentService.deleteApartment(apartmentId)));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity integrityViolation(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(ex.getMessage(), HttpStatus.CONFLICT.toString()));
    }
}
