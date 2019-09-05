package com.creatix.controller.v1.account;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.account.*;
import com.creatix.domain.dto.PageableWithTotalCountDataResponse;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.AuthorizationManager;
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
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping(path = {"/api/users", "/api/v1/users"})
@ApiVersion(1.0)
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
    @JsonView(Views.Public.class)
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security, AccountRole.Maintenance})
    public PageableWithTotalCountDataResponse<List<AccountDto>> getAccounts(@RequestParam(required = false) AccountRole[] roles, 
    		@RequestParam(required = false) Long propertyId,
    		@RequestParam(value="page",required=false) Integer page, 
    		@RequestParam(value="size",required=false) Integer size, 
    		@RequestParam(value="keywords",required=false) String keywords,
    		@RequestParam(value="sortColumn",required=false) String sortColumn, 
    		@RequestParam(value="sortOrder",required=false) String sortOrder) {
    	
    	List<Account> accountsAll = accountService.getAccounts(roles, propertyId, keywords, sortColumn, sortOrder);
    	
        return new PageableWithTotalCountDataResponse<List<AccountDto>>(accountService.getAccountsPage(accountsAll, size, page).stream()
                .map(a -> mapper.toAccountDto(a))
                .collect(Collectors.toList()), size, page, accountsAll.size(), 10);
    }

    @ApiOperation(value = "Get self profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
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
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{accountId}/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> getProfile(@PathVariable Long accountId) {
        return new DataResponse<>(mapper.toAccountDto(accountService.getAccount(accountId)));
    }

    @ApiOperation(value = "Update self profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RoleSecured
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/me/profile", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateSelfProfile(@Valid @RequestBody UpdateAccountProfileRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateAccount(authorizationManager.getCurrentAccount(), request)));
    }

    @ApiOperation(value = "Change password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/me/password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateAccountPasswordFromRequest(request)));
    }

    @ApiOperation(value = "First time set password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/me/password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> setPassword(@Valid @RequestBody CreatePasswordRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.createAccountPasswordFromRequest(request)));
    }

    @ApiOperation(value = "Update user profile information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{accountId}/profile", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateProfile(@PathVariable Long accountId, @Valid @RequestBody UpdateAccountProfileRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateAccountFromRequest(accountId, request)));
    }

    @ApiOperation(value = "Delete account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{accountId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public DataResponse<AccountDto> deleteAccount(@PathVariable Long accountId) {
        return new DataResponse<>(mapper.toAccountDto(accountService.deleteAccount(accountId)));
    }

    @ApiOperation(value = "Create administrator account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured(AccountRole.Administrator)
    @RequestMapping(value = "/administrators", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createAdministrator(@Valid @RequestBody PersistAdministratorRequest request) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createAdministrator(request)));
    }

    @ApiOperation(value = "Update administrator account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured(AccountRole.Administrator)
    @RequestMapping(value = "/administrators/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateAdministrator(@PathVariable Long accountId, @Valid @RequestBody PersistAdministratorRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateAdministrator(accountId, request)));
    }

    @ApiOperation(value = "Create property owner account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured(AccountRole.Administrator)
    @RequestMapping(value = "/property-owners", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createPropertyOwner(@Valid @RequestBody PersistPropertyOwnerRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createPropertyOwner(request)));
    }

    @ApiOperation(value = "Update property owner account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured(AccountRole.Administrator)
    @RequestMapping(value = "/property-owners/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updatePropertyOwner(@PathVariable Long accountId, @Valid @RequestBody PersistPropertyOwnerRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updatePropertyOwner(accountId, request)));
    }

    @ApiOperation(value = "Create property manager account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager})
    @RequestMapping(value = "/property-managers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createPropertyManager(@Valid @RequestBody PersistPropertyManagerRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createPropertyManager(request)));
    }

    @ApiOperation(value = "Update property manager account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager})
    @RequestMapping(value = "/property-managers/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updatePropertyManager(@PathVariable Long accountId, @Valid @RequestBody PersistPropertyManagerRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updatePropertyManager(accountId, request)));
    }

    @ApiOperation(value = "Create security guy account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager})
    @RequestMapping(value = "/security-guys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createSecurityGuy(@Valid @RequestBody PersistSecurityGuyRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createSecurityGuy(request)));
    }

    @ApiOperation(value = "Update security guy account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.Security})
    @RequestMapping(value = "/security-guys/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateSecurityGuy(@PathVariable Long accountId, @Valid @RequestBody PersistSecurityGuyRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateSecurityGuy(accountId, request)));
    }

    @ApiOperation(value = "Create maintenance guy account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager})
    @RequestMapping(value = "/maintenance-guys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createMaintenanceGuy(@Valid @RequestBody PersistMaintenanceGuyRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createMaintenanceGuy(request)));
    }

    @ApiOperation(value = "Update maintenance guy account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.Maintenance})
    @RequestMapping(value = "/maintenance-guys/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateMaintenanceGuy(@PathVariable Long accountId, @Valid @RequestBody PersistMaintenanceGuyRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateMaintenanceGuy(accountId, request)));
    }    
    
    @ApiOperation(value = "Create assistant property manager account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.PropertyOwner})
    @RequestMapping(value = "/assistant-property-managers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> createAssistantPropertyManager(@Valid @RequestBody PersistAssistantPropertyManagerRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        return new DataResponse<>(mapper.toAccountDto(accountService.createAssistantPropertyManager(request)));
    }

    @ApiOperation(value = "Update assistant property manager account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner})
    @RequestMapping(value = "/assistant-property-managers/{accountId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateAssistantPropertyManager(@PathVariable Long accountId, @Valid @RequestBody PersistAssistantPropertyManagerRequest request) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateAssistantPropertyManager(accountId, request)));
    }

    @ApiOperation(value = "Reset authentication code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    @RequestMapping(value = "/{accountId}/reset/code", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<String> resetCode(@PathVariable Long accountId) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        return new DataResponse<>(accountService.resetActivationCode(accountId));
    }

    @ApiOperation(value = "Switch sub-tenant to primary resident of apartment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/switch/{subTenantId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.AssistantPropertyManager, AccountRole.PropertyManager})
    public DataResponse<AccountDto> switchSubTenantToPrimaryTenant(@PathVariable Long subTenantId) {
        return new DataResponse<>(mapper.toAccountDto(accountService.switchSubTenantToPrimaryTenant(subTenantId)));
    }

    @ApiOperation(value = "Resend activation (re-generate if expired) code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "{accountId}/resend/code", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public ResponseEntity resendActivationCode(@PathVariable Long accountId) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        Account account = authorizationManager.getCurrentAccount();
        accountService.resendActivationCodeRequest(accountId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
