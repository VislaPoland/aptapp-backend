package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.account.*;
import com.creatix.domain.entity.account.Account;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.AccountService;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    @ApiOperation(value = "Get accounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<List<AccountDto>> getAccounts(@RequestParam(required = false) AccountRole[] roles, @RequestParam(required = false) Long propertyId) {
        return new DataResponse<>(accountService.getAccounts(roles, propertyId).stream()
                .map(a -> mapper.toAccountDto(a))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get self profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/me/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> getSelfProfile() {
        Account account = authorizationManager.getCurrentAccount();
        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @ApiOperation(value = "Get user profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> getProfile(@PathVariable Long accountId) {
        Account account = accountService.getAccount(accountId);
        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @ApiOperation(value = "Update self profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/me/profile", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateSelfProfile(@RequestBody @Valid UpdateAccountProfileRequest request) {
        Account account = authorizationManager.getCurrentAccount();
        account = accountService.updateAccount(account, request);

        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @ApiOperation(value = "Update user profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}/profile", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateProfile(@PathVariable Long accountId, @RequestBody @Valid UpdateAccountProfileRequest request) {
        Account account = accountService.getAccount(accountId);
        account = accountService.updateAccount(account, request);

        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @ApiOperation(value = "Reset authentication code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured(AccountRole.PropertyManager)
    @RequestMapping(value = "/{accountId}/reset-code", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<String> resetCode(@PathVariable Long accountId) {
        final Account account = accountService.getAccount(accountId);
        accountService.setActionToken(account);
        return new DataResponse<>(account.getActionToken());
    }





    @ApiOperation(value = "Create administrator account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured(AccountRole.Administrator)
    @RequestMapping(value = "/administrators", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createAdministrator(@RequestBody @Valid PersistAdministratorRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.createAdministrator(request)));
    }

    @ApiOperation(value = "Update administrator account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured(AccountRole.Administrator)
    @RequestMapping(value = "/administrators/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateAdministrator(@PathVariable Long accountId, @RequestBody @Valid PersistAdministratorRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateAdministrator(accountId, request)));
    }

    @ApiOperation(value = "Create property owner account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured(AccountRole.Administrator)
    @RequestMapping(value = "/property-owners", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createPropertyOwner(@RequestBody @Valid PersistPropertyOwnerRequest request) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createPropertyOwner(request)));
    }

    @ApiOperation(value = "Update property owner account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured(AccountRole.Administrator)
    @RequestMapping(value = "/property-owners/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updatePropertyOwner(@PathVariable Long accountId, @RequestBody @Valid PersistPropertyOwnerRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updatePropertyOwner(accountId, request)));
    }

    @ApiOperation(value = "Create property manager account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    @RequestMapping(value = "/property-managers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createPropertyManager(@RequestBody @Valid PersistPropertyManagerRequest request) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createPropertyManager(request)));
    }

    @ApiOperation(value = "Update property manager account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    @RequestMapping(value = "/property-managers/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updatePropertyManager(@PathVariable Long accountId, @RequestBody @Valid PersistPropertyManagerRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updatePropertyManager(accountId, request)));
    }

    @ApiOperation(value = "Create security guy account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured({AccountRole.PropertyManager})
    @RequestMapping(value = "/security-guys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createSecurityGuy(@RequestBody @Valid PersistSecurityGuyRequest request) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createSecurityGuy(request)));
    }

    @ApiOperation(value = "Update security guy account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured({AccountRole.PropertyManager, AccountRole.Security})
    @RequestMapping(value = "/security-guys/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateSecurityGuy(@PathVariable Long accountId, @RequestBody @Valid PersistSecurityGuyRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateSecurityGuy(accountId, request)));
    }

    @ApiOperation(value = "Create maintenance guy account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured({AccountRole.PropertyManager})
    @RequestMapping(value = "/maintenance-guys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createMaintenanceGuy(@RequestBody @Valid PersistMaintenanceGuyRequest request) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createMaintenanceGuy(request)));
    }

    @ApiOperation(value = "Update maintenance guy account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured({AccountRole.PropertyManager, AccountRole.Maintenance})
    @RequestMapping(value = "/maintenance-guys/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateMaintenanceGuy(@PathVariable Long accountId, @RequestBody @Valid PersistMaintenanceGuyRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateMaintenanceGuy(accountId, request)));
    }    
    
    @ApiOperation(value = "Create assistant property manager account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured({AccountRole.PropertyManager})
    @RequestMapping(value = "/assistant-property-managers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createAssistantPropertyManager(@RequestBody @Valid PersistAssistantPropertyManagerRequest request) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createAssistantPropertyManager(request)));
    }

    @ApiOperation(value = "Update assistant property manager account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    @RequestMapping(value = "/assistant-property-managers/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateAssistantPropertyManager(@PathVariable Long accountId, @RequestBody @Valid PersistAssistantPropertyManagerRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateAssistantPropertyManager(accountId, request)));
    }    
}
