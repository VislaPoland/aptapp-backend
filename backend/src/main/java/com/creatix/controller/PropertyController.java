package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.account.UpdateAccountDto;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.entity.Account;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.AccountService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.bytecode.spi.NotInstrumentedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Transactional
@RequestMapping("/api/property")
public class PropertyController {

    @Autowired
    private Mapper mapper;

    @ApiOperation(value = "Get property details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PropertyDetailsDto> getPropertyDetails(@PathVariable long propertyId) {
        throw new NotInstrumentedException("This is not implemented yet");
    }
}
