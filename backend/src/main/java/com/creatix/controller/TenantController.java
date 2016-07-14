package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.tenant.PersistTenantRequest;
import com.creatix.domain.dto.tenant.TenantDto;
import com.creatix.domain.dto.tenant.parkingStall.ParkingStallDto;
import com.creatix.domain.dto.tenant.subs.SubTenantDto;
import com.creatix.domain.dto.tenant.subs.PersistSubTenantRequest;
import com.creatix.domain.dto.tenant.vehicle.AssignVehicleRequest;
import com.creatix.domain.dto.tenant.vehicle.VehicleDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.RoleSecured;
import com.creatix.service.TenantService;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
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
    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public DataResponse<TenantDto> createTenant(@RequestBody @Valid PersistTenantRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        return new DataResponse<>(mapper.toTenantDto(tenantService.createTenantFromRequest(request)));
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
    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public DataResponse<TenantDto> updateTenant(@PathVariable Long tenantId, @Valid @RequestBody PersistTenantRequest request) {
        return new DataResponse<>(mapper.toTenantDto(tenantService.updateTenantFromRequest(tenantId, request)));
    }

    @ApiOperation(value = "Delete tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public DataResponse<TenantDto> updateTenant(@PathVariable Long tenantId) {
        return new DataResponse<>(mapper.toTenantDto(tenantService.deleteTenant(tenantId)));
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

    @ApiOperation(value = "Delete tenant vehicle")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{tenantId}/vehicles/{vehicleId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Tenant, AccountRole.PropertyManager})
    public DataResponse<Void> deleteTenantVehicle(@PathVariable Long tenantId, @PathVariable Long vehicleId) {
        tenantService.deleteVehicle(tenantId, vehicleId);
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

    @ApiOperation(value = "Update vehicle parked at tenant assigned parking stall")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "/{tenantId}/parking-stalls/{parkingStallId}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Tenant})
    public DataResponse<ParkingStallDto> updateVehicleAtParkingStall(@PathVariable Long tenantId, @PathVariable Long parkingStallId,
                                                                     @RequestBody @Valid AssignVehicleRequest request) {
        return new DataResponse<>(mapper.toParkingStallDto(tenantService.updateVehicleAtParkingStall(tenantId, parkingStallId, request)));
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
    public DataResponse<SubTenantDto> createSubTenant(@PathVariable Long tenantId, @RequestBody @Valid PersistSubTenantRequest request) {
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
    public DataResponse<SubTenantDto> updateSubTenant(@PathVariable Long tenantId, @PathVariable Long subTenantId, @RequestBody @Valid PersistSubTenantRequest request) {
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
