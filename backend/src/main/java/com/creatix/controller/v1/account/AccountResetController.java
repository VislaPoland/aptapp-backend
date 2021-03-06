package com.creatix.controller.v1.account;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.ErrorMessage;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.account.AskResetPasswordRequest;
import com.creatix.domain.dto.account.ResetCodeRequest;
import com.creatix.domain.dto.account.ResetPasswordRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.RoleSecured;
import com.creatix.service.AccountService;
import com.fasterxml.jackson.annotation.JsonView;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@Transactional
@RequestMapping(path = {"/api/account", "/api/v1/account"})
@ApiVersion(1.0)
public class AccountResetController {

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "Reset authentication code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.PropertyOwner})
    @RequestMapping(value = "/reset/code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<String> resetCode(@Valid @RequestBody ResetCodeRequest request) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        return new DataResponse<>(accountService.resetActivationCode(request));
    }

    @ApiOperation(value = "Request for password reset")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/request-reset/password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> askPasswordReset(@Valid @RequestBody AskResetPasswordRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        accountService.resetPasswordFromRequest(request);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Reset password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/reset/password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        accountService.resetAccountPasswordFromRequest(request);
        return new DataResponse<>();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity integrityViolation(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(ex.getMessage(), HttpStatus.CONFLICT.toString()));
    }
}
