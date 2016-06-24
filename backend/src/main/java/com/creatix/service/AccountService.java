package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.dto.LoginResponse;
import com.creatix.domain.dto.account.UpdateAccountProfileRequest;
import com.creatix.domain.dto.tenant.CreateTenantRequest;
import com.creatix.domain.dto.tenant.UpdateTenantRequest;
import com.creatix.domain.entity.Account;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Tenant;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.*;
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
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    private ApartmentService apartmentService;

    private static void validatePassword(String password) {
        if ( StringUtils.isBlank(password) ) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if ( password.length() < 6 ) {
            throw new IllegalStateException("Password must be at least 6 characters long");
        }
    }

    public Tenant getTenant(long accountId) {
        final Account account = accountDao.findById(accountId);
        if ( account instanceof Tenant ) {
            return (Tenant) account;
        }

        throw new EntityNotFoundException(String.format("Tenant id=%d not found", accountId));
    }

    @RoleSecured(AccountRole.PropertyManager)
    public Tenant createTenantFromRequest(@NotNull CreateTenantRequest request) {
        Objects.requireNonNull(request);

        final Apartment apartment = apartmentService.getApartment(request.getApartmentId());
        authorizationManager.checkManager(apartment.getProperty());


        final Tenant tenant = mapper.toTenant(request);
        tenant.setApartment(apartment);
        tenant.setActive(false);
        accountDao.persist(tenant);

        setActionToken(tenant);

        return tenant;
    }

    @RoleSecured(AccountRole.PropertyManager)
    public Tenant updateTenantFromRequest(long tenantId, @NotNull UpdateTenantRequest request) {
        Objects.requireNonNull(request);

        final Apartment apartment = apartmentService.getApartment(request.getApartmentId());
        authorizationManager.checkManager(apartment.getProperty());


        final Tenant tenant = getTenant(tenantId);
        mapper.fillTenant(request, tenant);
        tenant.setApartment(apartment);
        accountDao.persist(tenant);

        return tenant;
    }

    public Account getAccount(long accouuntId) {
        return accountDao.findById(accouuntId);
    }

    public void setActionToken(@NotNull  Account account) {
        Objects.requireNonNull(account);

        account.setActionToken(RandomStringUtils.randomNumeric(12));
        account.setActionTokenValidUntil(DateTime.now().plusDays(7).toDate());

        accountDao.persist(account);
    }

    private static void checkTokenValidity(String token, Account account) {
        if ( StringUtils.isBlank(token) ) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
        if ( account.getActionTokenValidUntil() == null ) {
            throw new IllegalStateException("Missing token valid until");
        }
        if ( !(StringUtils.equalsIgnoreCase(token, account.getActionToken())) ) {
            throw new SecurityException("Token mismatch");
        }
        if ( account.getActionTokenValidUntil().before(new Date()) ) {
            throw new SecurityException("Token has expired");
        }
    }

    public LoginResponse createLoginResponse(String email) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = tokenUtils.generateToken(userDetails);


        final LoginResponse result = new LoginResponse();
        result.setToken(token);
        final Account account = ((AuthenticatedUserDetails) userDetails).getAccount();
        result.setId(account.getId());
        result.setAccount(mapper.toAccountDto(account));

        return result;
    }

    public void authenticate(String email, String password) throws AuthenticationException {
        Account account = getAccount(email);
        if (!account.getActive()) {
            throw new SecurityException("Account not activated");
        }

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @RoleSecured(AccountRole.Administrator)
    public List<Account> getAccounts(AccountRole[] roles) {
        if ( (roles == null) || (roles.length == 0) ) {
            return accountDao.findAll();
        }
        else {
            return accountDao.findByRoles(roles);
        }
    }

    public Account getAccount(String email) {
        final  Account account = accountDao.findByEmail(email);
        if (account == null) {
            throw new EntityNotFoundException(String.format("Account with email=%s not found", email));
        }
        return account;
    }

    public Account getAccount(Long accountId) {
        final Account account = accountDao.findById(accountId);
        if ( account == null ) {
            throw new EntityNotFoundException(String.format("Account id=%d not found", accountId));
        }
        return account;
    }

    public Account activateAccount(String activationCode) {
        final Account account = accountDao.findByActionToken(activationCode);
        if ( account == null ) {
            throw new SecurityException("Activation code not valid");
        }
        if ( account.getActive() ) {
            return account;
        }

        if ( account.getActionTokenValidUntil().before(DateTime.now().toDate()) ) {
            throw new SecurityException("Activation code has expired");
        }

        account.setActive(true);
        accountDao.persist(account);

        return account;
    }

    public Account updateAccount(Account account, UpdateAccountProfileRequest accountDto) {
        account.setSecondaryEmail(accountDto.getSecondaryEmail());
        account.setSecondaryPhone(accountDto.getSecondaryPhone());
        if ( StringUtils.isNotBlank(accountDto.getPassword()) ) {
            account.setPasswordHash(passwordEncoder.encode(accountDto.getPassword()));
        }

        accountDao.persist(account);

        return account;
    }
}