package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.dto.LoginResponse;
import com.creatix.domain.entity.Account;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthenticatedUserDetailsService;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.TokenUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private AuthenticatedUserDetailsService userDetailsService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private Mapper mapper;

    private static void validatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 6) {
            throw new IllegalStateException("Password must be at least 6 characters long");
        }
    }

    private static void setActionToken(Account account) {
        if (account == null) {
            throw new NullPointerException("Reference to account is null");
        }

        account.setActionToken(RandomStringUtils.randomAlphanumeric(64));
        account.setActionTokenValidUntil(new DateTime().plusDays(1).toDate());
    }

    private static void checkTokenValidity(String token, Account account) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
        if (account.getActionTokenValidUntil() == null) {
            throw new IllegalStateException("Missing token valid until");
        }
        if (!(StringUtils.equalsIgnoreCase(token, account.getActionToken()))) {
            throw new SecurityException("Token mismatch");
        }
        if (account.getActionTokenValidUntil().before(new Date())) {
            throw new SecurityException("Token has expired");
        }
    }

    public LoginResponse createLoginResponse(String email) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = tokenUtils.generateToken(userDetails);


        final LoginResponse result = new LoginResponse();
        result.setToken(token);
        final Account account = authorizationManager.getCurrentAccount();
        result.setId(account.getId());

        return result;
    }

    public void authenticate(String email, String password) throws AuthenticationException {

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public Account getAccount(long accountId) {
        final Account account = accountDao.findById(accountId);
        if (account == null) {
            throw new EntityNotFoundException(String.format("Account id=%d not found", accountId));
        }
        return account;
    }

    public Account saveAccount(Account account) {
        accountDao.persist(account);
        return accountDao.findByEmail(account.getEmail());
    }

    //TODO delete test
    public Account createTestAccount() {
        Account account = new Account();
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setCompanyName("Company name");
        account.setPhone("123456789");
        account.setEmail("john.doe@mail.com");
        account.setPasswordHash(passwordEncoder.encode("password"));
        account.setRole(AccountRole.Maintenance);
        account.setActive(true);

        return account;
    }
}