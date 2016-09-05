package com.creatix.controller;

import com.creatix.domain.dto.*;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.security.RoleSecured;
import com.creatix.service.AccountService;
import com.fasterxml.jackson.annotation.JsonView;
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
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "Verify authentication code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/verify-code", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<LoginResponse> verifyCode(@RequestBody @Valid ActivationCode codeRequest) {
        Account activatedAccount = accountService.activateAccount(codeRequest.getCode());

        return new DataResponse<>(accountService.createLoginResponse(activatedAccount.getPrimaryEmail()));
    }

    @ApiOperation(value = "Attempt to sign in")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/attempt", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<LoginResponse> signIn(@RequestBody @Valid LoginRequest loginRequest) {
        accountService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return new DataResponse<>(accountService.createLoginResponse(loginRequest.getEmail()));
    }

    @ApiOperation(value = "Logout")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @JsonView(Views.Public.class)
    @RoleSecured
    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> logout() {
        accountService.logout();
        return new DataResponse<>();
    }
}
