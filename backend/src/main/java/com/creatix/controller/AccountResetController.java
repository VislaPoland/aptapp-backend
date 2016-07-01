package com.creatix.controller;

import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.entity.account.Account;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.RoleSecured;
import com.creatix.service.AccountService;
import freemarker.template.TemplateException;
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

import java.io.IOException;

@RestController
@Transactional
@RequestMapping("/api/users/{accountId}/reset")
public class AccountResetController {

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "Reset authentication code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured(AccountRole.PropertyManager)
    @RequestMapping(value = "/code", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<String> resetCode(@PathVariable Long accountId) {
        final Account account = accountService.getAccount(accountId);
        accountService.setActionToken(account);
        return new DataResponse<>(account.getActionToken());
    }

    @ApiOperation(value = "Reset password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured()
    @RequestMapping(value = "/password", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Boolean> resetPassword(@PathVariable Long accountId) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(accountService.resetPassword(accountId));
    }
    
}
