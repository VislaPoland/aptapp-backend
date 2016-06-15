package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.account.UpdateAccountDto;
import com.creatix.domain.entity.Account;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.AccountService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Transactional
@RequestMapping("/api/users")
public class AccountController {
    @Autowired
    private Mapper mapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AuthorizationManager authorizationManager;

    @ApiOperation(value = "Get user profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/me/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> getSelfProfile() {
        Account account = accountService.getAccount(authorizationManager.getCurrentAccount().getId());
        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @ApiOperation(value = "Update user profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/me/profile", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateSelfProfile(@RequestBody @Valid UpdateAccountDto updateAccountDto) {
        Account account = accountService.getAccount(authorizationManager.getCurrentAccount().getId());
        account = accountService.updateAccount(account, updateAccountDto);

        return new DataResponse<>(mapper.toAccountDto(account));
    }
}
