package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.LoginResponse;
import com.creatix.domain.dto.account.*;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.account.Account;
import com.creatix.domain.entity.account.Employee;
import com.creatix.domain.entity.account.PropertyManager;
import com.creatix.domain.entity.account.PropertyOwner;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.EmailMessageSender;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.template.ActivationMessageTemplate;
import com.creatix.security.*;
import freemarker.template.TemplateException;
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
import java.io.IOException;
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
    private AuthenticatedUserDetailsService userDetailsService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private Mapper mapper;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private PropertyOwnerDao propertyOwnerDao;
    @Autowired
    private PropertyManagerDao propertyManagerDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private EmailMessageSender emailMessageSender;
    @Autowired
    private EmployeeDao employeeDao;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if (item == null) {
            throw ex;
        }
        return item;
    }

    public void setActionToken(@NotNull Account account) {
        Objects.requireNonNull(account);

        account.setActionToken(RandomStringUtils.randomNumeric(12));
        account.setActionTokenValidUntil(DateTime.now().plusDays(7).toDate());

        accountDao.persist(account);
    }

    public LoginResponse createLoginResponse(String email) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = tokenUtils.generateToken(userDetails);


        final LoginResponse result = new LoginResponse();
        result.setToken(token);
        final Account account = ((AuthenticatedUserDetails) userDetails).getAccount();
        result.setId(account.getId());
        result.setAccount(mapper.toAccountDto(account));
        result.setRole(account.getRole());

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

    public List<Account> getAccounts(AccountRole[] roles, Long propertyId) {
        Long propertyIdForced = propertyId;
        if ( !(authorizationManager.isAdministrator()) ) {
            final Account account = authorizationManager.getCurrentAccount();
            if ( account instanceof PropertyManager ) {
                propertyIdForced = ((PropertyManager) account).getManagedProperty().getId();
            }
        }

        return accountDao.findByRolesAndPropertyId(roles, propertyIdForced);
    }

    private Account getAccount(String email) {
        final Account account = accountDao.findByEmail(email);
        if (account == null) {
            throw new EntityNotFoundException(String.format("Account with email=%s not found", email));
        }
        return account;
    }

    public Account getAccount(Long accountId) {
        return getOrElseThrow(accountId, accountDao, new EntityNotFoundException(String.format("Account id=%d not found", accountId)));
    }

    public Account activateAccount(String activationCode) {
        final Account account = accountDao.findByActionToken(activationCode);
        if (account == null) {
            throw new SecurityException("Activation code not valid");
        }
        if (account.getActive()) {
            return account;
        }

        if (account.getActionTokenValidUntil() == null || account.getActionTokenValidUntil().before(DateTime.now().toDate())) {
            throw new SecurityException("Activation code has expired");
        }

        account.setActive(true);
//        account.setActionToken(null);
//        account.setActionTokenValidUntil(null);
        accountDao.persist(account);

        return account;
    }

    public Account updateAccount(Account account, UpdateAccountProfileRequest accountDto) {
        account.setSecondaryEmail(accountDto.getSecondaryEmail());
        account.setSecondaryPhone(accountDto.getSecondaryPhone());
        if (StringUtils.isNotBlank(accountDto.getPassword())) {
            account.setPasswordHash(passwordEncoder.encode(accountDto.getPassword()));
        }

        accountDao.persist(account);

        return account;
    }

    @RoleSecured(AccountRole.Administrator)
    public Account createAdministrator(@NotNull PersistAdministratorRequest request) {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail(), null);

        final Account account = new Account();
        account.setRole(AccountRole.Administrator);
        mapper.fillAccount(request, account);
        account.setActive(true);
        accountDao.persist(account);

        return account;
    }

    @RoleSecured(AccountRole.Administrator)
    public Account updateAdministrator(@NotNull Long accountId, @NotNull PersistAdministratorRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        final Account account = getAccount(accountId);
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.Administrator ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        accountDao.persist(account);

        return account;
    }

    @RoleSecured(AccountRole.Administrator)
    public Account createPropertyOwner(@NotNull PersistPropertyOwnerRequest request) throws MessageDeliveryException, TemplateException, IOException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail(), null);

        final PropertyOwner account = new PropertyOwner();
        account.setRole(AccountRole.PropertyOwner);
        mapper.fillAccount(request, account);
        account.setActive(false);
        accountDao.persist(account);
        setActionToken(account);

        emailMessageSender.send(new ActivationMessageTemplate(account));

        return account;
    }

    @RoleSecured(AccountRole.Administrator)
    public Account updatePropertyOwner(@NotNull Long accountId, @NotNull PersistPropertyOwnerRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        final PropertyOwner account = propertyOwnerDao.findById(accountId);
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.PropertyOwner ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        propertyOwnerDao.persist(account);

        return account;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public PropertyManager createPropertyManager(@NotNull PersistPropertyManagerRequest request) throws MessageDeliveryException, TemplateException, IOException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail(), null);

        Property managedProperty = null;
        if (authorizationManager.getCurrentAccount().getRole() == AccountRole.PropertyOwner) {
            Objects.requireNonNull(request.getManagedPropertyId());

            managedProperty = propertyDao.findById(request.getManagedPropertyId());
            authorizationManager.checkOwner(managedProperty);
        }
        else {
            managedProperty = authorizationManager.getCurrentProperty();
        }

        final PropertyManager account = new PropertyManager();
        account.setRole(AccountRole.PropertyManager);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setManagedProperty(managedProperty);
        accountDao.persist(account);
        setActionToken(account);

        emailMessageSender.send(new ActivationMessageTemplate(account));

        return account;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public PropertyManager updatePropertyManager(@NotNull Long accountId, @NotNull PersistPropertyManagerRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        Property managedProperty = null;
        PropertyManager account = null;
        if (authorizationManager.getCurrentAccount().getRole() == AccountRole.PropertyOwner) {
            Objects.requireNonNull(request.getManagedPropertyId());

            managedProperty = propertyDao.findById(request.getManagedPropertyId());
            authorizationManager.checkOwner(managedProperty);
            account = propertyManagerDao.findById(accountId);
        }
        else {
            managedProperty = authorizationManager.getCurrentProperty();
            account = propertyManagerDao.findById(accountId);
            authorizationManager.isSelf(account);
        }
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.PropertyManager ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        account.setManagedProperty(managedProperty);
        propertyManagerDao.persist(account);

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager})
    public Employee createSecurityGuy(@NotNull PersistSecurityGuyRequest request) throws MessageDeliveryException, TemplateException, IOException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail(), null);

        final PropertyManager manager = (PropertyManager) authorizationManager.getCurrentAccount();

        final Employee account = new Employee();
        account.setRole(AccountRole.Security);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setManager(manager);
        employeeDao.persist(account);
        setActionToken(account);

        emailMessageSender.send(new ActivationMessageTemplate(account));

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.Security})
    public Employee updateSecurityGuy(@NotNull Long accountId, @NotNull PersistSecurityGuyRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        final Employee account = employeeDao.findById(accountId);
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.Security ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        employeeDao.persist(account);

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner})
    public Employee createMaintenanceGuy(@NotNull PersistMaintenanceGuyRequest request) throws MessageDeliveryException, TemplateException, IOException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail(), null);

        final PropertyManager manager = (PropertyManager) authorizationManager.getCurrentAccount();

        final Employee account = new Employee();
        account.setRole(AccountRole.Maintenance);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setManager(manager);
        employeeDao.persist(account);
        setActionToken(account);

        emailMessageSender.send(new ActivationMessageTemplate(account));

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.Maintenance})
    public Employee updateMaintenanceGuy(@NotNull Long accountId, @NotNull PersistMaintenanceGuyRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        final Employee account = employeeDao.findById(accountId);
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.Maintenance ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        employeeDao.persist(account);

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager})
    public Employee createAssistantPropertyManager(@NotNull PersistAssistantPropertyManagerRequest request) throws MessageDeliveryException, TemplateException, IOException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail(), null);

        final PropertyManager manager = (PropertyManager) authorizationManager.getCurrentAccount();

        final Employee account = new Employee();
        account.setRole(AccountRole.AssistantPropertyManager);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setManager(manager);
        employeeDao.persist(account);
        setActionToken(account);

        emailMessageSender.send(new ActivationMessageTemplate(account));

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public Employee updateAssistantPropertyManager(@NotNull Long accountId, @NotNull PersistAssistantPropertyManagerRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        final Employee account = employeeDao.findById(accountId);
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.AssistantPropertyManager ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        employeeDao.persist(account);

        return account;
    }


    private void preventAccountDuplicity(String email, String emailExisting) {
        if ( Objects.equals(email, emailExisting) ) {
            // email will not change, assume account is not a duplicate
            return;
        }

        if ( accountDao.findByEmail(email) != null ) {
            throw new IllegalArgumentException(String.format("Account %s already exists.", email));
        }
    }


}