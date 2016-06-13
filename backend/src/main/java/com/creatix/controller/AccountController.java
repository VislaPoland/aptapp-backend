package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.AccountDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.UpdateAccountDto;
import com.creatix.domain.entity.Account;
import com.creatix.security.AuthorizationManager;
import com.creatix.service.AccountService;
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

    @RequestMapping(value = "/me/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> getSelfProfile() {
        Account account = accountService.getAccount(authorizationManager.getCurrentAccount().getId());
        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @RequestMapping(value = "/me/profile", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> updateSelfProfile(@RequestBody @Valid UpdateAccountDto updateAccountDto) {
        Account account = accountService.getAccount(authorizationManager.getCurrentAccount().getId());
        account = accountService.updateAccount(account, updateAccountDto);

        return new DataResponse<>(mapper.toAccountDto(account));
    }
}
