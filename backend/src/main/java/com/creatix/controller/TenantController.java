package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.tenant.CreateTenantRequest;
import com.creatix.domain.dto.tenant.UpdateTenantRequest;
import com.creatix.service.AccountService;
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
@RequestMapping("/api/tenant")
public class TenantController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private Mapper mapper;

    @ApiOperation(value = "Create tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createTenant(@RequestBody @Valid CreateTenantRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.createTenantFromRequest(request)));
    }

    @ApiOperation(value = "Update tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateTenant(@PathVariable long accountId, @Valid @RequestBody UpdateTenantRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateTenantFromRequest(accountId, request)));
    }
}
