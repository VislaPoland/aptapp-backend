package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.apartment.PersistApartmentRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.apartment.ApartmentService;
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
@RequestMapping("/api/properties/{propertyId}")
public class ApartmentController {

    @Autowired
    private Mapper mapper;
    @Autowired
    private ApartmentService apartmentService;

    @ApiOperation(value = "Get apartment details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
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
    @RequestMapping(value = "/apartments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.Administrator})
    public DataResponse<ApartmentDto> createApartment(@PathVariable Long propertyId, @Valid @RequestBody PersistApartmentRequest request) {
        return new DataResponse<>(mapper.toApartmentDto(apartmentService.createApartment(propertyId, request)));
    }

    @ApiOperation(value = "Update apartment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/apartments/{apartmentId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.Administrator})
    public DataResponse<ApartmentDto> updateApartment(@PathVariable Long apartmentId, @Valid @RequestBody PersistApartmentRequest request) {
        return new DataResponse<>(mapper.toApartmentDto(apartmentService.updateApartment(apartmentId, request)));
    }
}
