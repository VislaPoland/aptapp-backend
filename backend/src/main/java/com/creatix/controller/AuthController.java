package com.creatix.controller;

import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.LoginRequest;
import com.creatix.domain.dto.LoginResponse;
import com.creatix.domain.dto.VerificationCode;
import com.creatix.security.AuthorizationManager;
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
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorizationManager authorizationManager;

    @ApiOperation(value = "Verify authentication code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @RequestMapping(value = "/verify-code", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> verifyCode(@RequestBody VerificationCode codeRequest) {
        //TODO implement actual code check  (it will be unique for users)
        return new DataResponse<>();
    }

    @ApiOperation(value = "Attempt to sign in")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @RequestMapping(value = "/attempt", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse signIn(@RequestBody @Valid LoginRequest loginRequest) {
        accountService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return accountService.createLoginResponse(loginRequest.getEmail());
    }
}
