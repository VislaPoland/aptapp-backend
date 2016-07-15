package com.creatix.controller;

import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.mapper.ApartmentMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.apartment.ApartmentTenantService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
@RequestMapping(value = "/api/apartments/{apartmentId}/tenants")
public class ApartmentTenantController {

    @Autowired
    private ApartmentMapper mapper;
    @Autowired
    private ApartmentTenantService apartmentTenantService;

    @ApiOperation(value = "Remove property tenant from property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<ApartmentDto.TenantDto> removeApartmentTenant(@PathVariable Long apartmentId, @PathVariable Long tenantId) {
        return new DataResponse<>(mapper.toApartmentTenant(apartmentTenantService.delete(apartmentId, tenantId)));
    }

}
