package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.tenant.CreateTenantRequest;
import com.creatix.domain.dto.tenant.TenantDto;
import com.creatix.domain.dto.tenant.TenantSelfUpdateRequest;
import com.creatix.domain.dto.tenant.parkingStall.ParkingStallDto;
import com.creatix.domain.dto.tenant.subs.CreateSubTenantRequest;
import com.creatix.domain.dto.tenant.subs.SubTenantDto;
import com.creatix.domain.dto.tenant.subs.UpdateSubTenantRequest;
import com.creatix.domain.dto.tenant.vehicle.CreateVehicleRequest;
import com.creatix.domain.dto.tenant.vehicle.UpdateVehicleRequest;
import com.creatix.domain.dto.tenant.vehicle.VehicleDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.TenantService;
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
@RequestMapping("/api/tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;
    @Autowired
    private Mapper mapper;

    @ApiOperation(value = "Create tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<TenantDto> createTenant(@RequestBody @Valid CreateTenantRequest request) {
        return new DataResponse<>(mapper.toTenantDto(tenantService.createTenantFromRequest(request)));
    }

    @ApiOperation(value = "Get tenant profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<TenantDto> getTenant(@PathVariable Long tenantId) {
        return new DataResponse<>(mapper.toTenantDto(tenantService.getTenant(tenantId)));
    }

    @ApiOperation(value = "Update tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant})
    public DataResponse<TenantDto> updateTenant(@PathVariable Long tenantId, @Valid @RequestBody TenantSelfUpdateRequest request) {
        return new DataResponse<>(mapper.toTenantDto(tenantService.updateTenantFromRequest(tenantId, request)));
    }

    @ApiOperation(value = "Get tenant vehicles")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}/vehicles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<VehicleDto>> getTenantVehicles(@PathVariable Long tenantId) {
        return new DataResponse<>(tenantService.getTenantVehicles(tenantId).stream()
                .map(v -> mapper.toVehicleDto(v))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Create tenant vehicle")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{tenantId}/vehicles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Tenant})
    public DataResponse<VehicleDto> createTenantVehicle(@PathVariable Long tenantId, @RequestBody @Valid CreateVehicleRequest request) {
        return new DataResponse<>(mapper.toVehicleDto(tenantService.createVehicleFromRequest(tenantId, request)));
    }

    @ApiOperation(value = "Update tenant vehicles")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{tenantId}/vehicles/{licensePlate}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Tenant})
    public DataResponse<VehicleDto> updateTenantVehicle(@PathVariable Long tenantId, @PathVariable String licensePlate, @RequestBody @Valid UpdateVehicleRequest request) {
        return new DataResponse<>(mapper.toVehicleDto(tenantService.updateVehicleFromRequest(tenantId, licensePlate, request)));
    }

    @ApiOperation(value = "Delete tenant vehicle")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}/vehicles/{licensePlate}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Tenant})
    public DataResponse<Void> deleteTenantVehicle(@PathVariable Long tenantId, @PathVariable String licensePlate) {
        tenantService.deleteVehicle(tenantId, licensePlate);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Get tenant assigned parking stalls")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}/parking-stalls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<ParkingStallDto>> getAssignedParkingStalls(@PathVariable Long tenantId) {
        return new DataResponse<>(tenantService.getTenantParkingStalls(tenantId).stream()
                .map(ps -> mapper.toParkingStallDto(ps))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get sub-tenants created by tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}/subs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<SubTenantDto>> getSubTenants(@PathVariable Long tenantId) {
        return new DataResponse<>(tenantService.getSubTenants(tenantId).stream()
                .map(t -> mapper.toSubTenantDto(t))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Create sub-tenant account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{tenantId}/subs", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Tenant})
    public DataResponse<SubTenantDto> createSubTenant(@PathVariable Long tenantId, @RequestBody @Valid CreateSubTenantRequest request) {
        return new DataResponse<>(mapper.toSubTenantDto(tenantService.createSubTenant(tenantId, request)));
    }

    @ApiOperation(value = "Get single sub-tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}/subs/{subTenantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<SubTenantDto> getSubTenant(@PathVariable Long tenantId, @PathVariable Long subTenantId) {
        return new DataResponse<>(mapper.toSubTenantDto(tenantService.getSubTenant(subTenantId)));
    }

    @ApiOperation(value = "Update sub-tenant account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{tenantId}/subs/{subTenantId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Tenant})
    public DataResponse<SubTenantDto> updateSubTenant(@PathVariable Long tenantId, @PathVariable Long subTenantId, @RequestBody @Valid UpdateSubTenantRequest request) {
        return new DataResponse<>(mapper.toSubTenantDto(tenantService.updateSubTenant(tenantId, subTenantId, request)));
    }

    @ApiOperation(value = "Delete sub-tenant account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}/subs/{subTenantId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Tenant})
    public DataResponse<Void> deleteSubTenant(@PathVariable Long tenantId, @PathVariable Long subTenantId) {
        tenantService.deleteSubTenant(tenantId, subTenantId);
        return new DataResponse<>();
    }
}
