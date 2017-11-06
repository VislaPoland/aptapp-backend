package com.creatix.service;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.configuration.DeviceProperties;
import com.creatix.domain.Mapper;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.LoginResponse;
import com.creatix.domain.dto.account.*;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.email.*;
import com.creatix.security.*;
import com.creatix.service.message.EmailMessageService;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AccountService {

    @Autowired
    private DeviceDao deviceDao;
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
    private EmailMessageService emailMessageService;
    @Autowired
    private AccountDeviceService accountDeviceService;
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private AssistantPropertyManagerDao assistantPropertyManagerDao;
    @Autowired
    private SecurityEmployeeDao securityEmployeeDao;
    @Autowired
    private MaintenanceEmployeeDao maintenanceEmployeeDao;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private DeviceProperties deviceProperties;
    @Autowired
    private SmsMessageSender smsMessageSender;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }

    public void setActionToken(@NotNull Account account) {
        Objects.requireNonNull(account);

        final RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9').build();

        account.setActionToken(generator.generate(6));
        account.setActionTokenValidUntil(DateTime.now().plusDays(7).toDate());

        accountDao.persist(account);
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public String resetActivationCode(@NotNull ResetCodeRequest request) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        Objects.requireNonNull(request);

        final Long accountId = request.getAccountId();
        final Account account = getOrElseThrow(accountId, accountDao, new EntityNotFoundException(String.format("Account id=%d not found", accountId)));

        return resetActivationCode(request.getAccountId());
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public String resetActivationCode(@NotNull Long accountId) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        Objects.requireNonNull(accountId, "Account ID is null");

        final Account account = getOrElseThrow(accountId, accountDao, new EntityNotFoundException(String.format("Account id=%d not found", accountId)));
        authorizationManager.checkResetActivationCode(account);

        if ( account.getActive() == Boolean.TRUE ) {
            throw new IllegalArgumentException(String.format("Account id=%d is already activated", accountId));
        }

        setActionToken(account);

        emailMessageService.send(new ResetActivationMessageTemplate(account, applicationProperties));

        return account.getActionToken();
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
        if ( !account.getActive() ) {
            throw new SecurityException("Account not activated");
        }

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        accountDeviceService.assignDeviceToAccount(account);
    }

    public List<Account> getAccounts(AccountRole[] roles, Long propertyId) {
        List<Long> propertyIdForcedList = new ArrayList<>();
        if ( propertyId != null ) {
            propertyIdForcedList.add(propertyId);
        }

        if ( !(authorizationManager.isAdministrator()) ) {
            final Account account = authorizationManager.getCurrentAccount();
            if ( account instanceof PropertyManager ) {
                final Long propertyIdForced = ((PropertyManager) account).getManagedProperty().getId();
                propertyIdForcedList.add(propertyIdForced);
            }
            else if ( account instanceof AssistantPropertyManager ) {
                final Long propertyIdForced = ((AssistantPropertyManager) account).getManager().getManagedProperty().getId();
                propertyIdForcedList.add(propertyIdForced);
            }
            else if ( account instanceof PropertyOwner ) {
                ((PropertyOwner) account).getOwnedProperties().forEach(p -> propertyIdForcedList.add(p.getId()));
            }
            else if ( account instanceof ManagedEmployee ) {
                final Long propertyIdForced = ((ManagedEmployee) account).getManager().getManagedProperty().getId();
                propertyIdForcedList.add(propertyIdForced);
            }
        }

        return accountDao.findByRolesAndPropertyIdList(roles, propertyIdForcedList);
    }

    private Account getAccount(String email) {
        final Account account = accountDao.findByEmail(email);
        if ( account == null ) {
            throw new EntityNotFoundException(String.format("Account email=%s not found", email));
        }
        return account;
    }

    public Account getAccount(Long accountId) {
        return getOrElseThrow(accountId, accountDao, new EntityNotFoundException(String.format("Account id=%d not found", accountId)));
    }

    private Account getAccountByToken(String actionToken) {
        final Account account = accountDao.findByActionToken(actionToken);
        if ( account == null ) {
            throw new SecurityException(String.format("Action token=%s is not valid", actionToken));
        }
        return account;
    }

    public Account activateAccount(String activationCode) {
        final Account account = getAccountByToken(activationCode);
        if ( account.getActive() ) {
            return account;
        }

        if ( account.getActionTokenValidUntil() == null || account.getActionTokenValidUntil().before(DateTime.now().toDate()) ) {
            throw new SecurityException("Activation code has expired");
        }

        account.setActive(true);
        account.setActionToken(null);
        account.setActionTokenValidUntil(null);
        accountDao.persist(account);

        return account;
    }

    @RoleSecured
    public Account updateAccount(@NotNull Account account, @NotNull UpdateAccountProfileRequest request) {
        Objects.requireNonNull(account, "Account is null");
        Objects.requireNonNull(request, "Request is null");

        if ( account instanceof Tenant ) {
            if ( request.getEnableSms() == null ) {
                throw new IllegalArgumentException("Enable sms parameter is required");
            }
            final Tenant tenant = (Tenant) account;
            tenant.setEnableSms(request.getEnableSms());
        }

        mapper.fillAccount(request, account);
        checkPhoneNumber(account);
        accountDao.persist(account);

        return account;
    }

    @RoleSecured
    public Account updateAccountFromRequest(long accountId, @NotNull UpdateAccountProfileRequest request) {
        Objects.requireNonNull(request);

        final Account account = getAccount(accountId);
        if ( authorizationManager.isSelf(account) ) {
            return updateAccount(account, request);
        }
        else {
            throw new SecurityException(String.format("You are not eligible to change user=%d password", accountId));
        }
    }

    @RoleSecured
    public Account updateAccountPasswordFromRequest(@NotNull UpdatePasswordRequest request) {
        Objects.requireNonNull(request);

        final Account account = authorizationManager.getCurrentAccount();
        if ( passwordEncoder.matches(request.getOldPassword(), account.getPasswordHash()) ) {
            authenticate(account.getPrimaryEmail(), request.getOldPassword());
            account.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            accountDao.persist(account);
            return account;
        }
        else {
            throw new IllegalArgumentException("Old password is not correct");
        }
    }

    @RoleSecured
    public Account createAccountPasswordFromRequest(@NotNull CreatePasswordRequest request) {
        Objects.requireNonNull(request);

        final Account account = authorizationManager.getCurrentAccount();
        if ( account.getPasswordHash() == null ) {
            account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            accountDao.persist(account);
            return account;
        }
        else {
            throw new AccessDeniedException("Password already set");
        }
    }

    public void resetAccountPasswordFromRequest(@NotNull ResetPasswordRequest request) {
        Objects.requireNonNull(request);

        final Account account = getAccountByToken(request.getToken());

        if ( account.getActionTokenValidUntil() == null || account.getActionTokenValidUntil().before(DateTime.now().toDate()) ) {
            throw new SecurityException(String.format("Token %s has expired", request.getToken()));
        }

        account.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        account.setActionToken(null);
        account.setActionTokenValidUntil(null);
        accountDao.persist(account);
    }

    @RoleSecured(AccountRole.Administrator)
    public Account createAdministrator(@NotNull PersistAdministratorRequest request) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail());

        final Account account = this.getEntityInstance(request.getPrimaryEmail(), Account.class);
        account.setRole(AccountRole.Administrator);
        mapper.fillAccount(request, account);
        account.setActive(true);
        account.setCreatedAt(new Date());
        checkPhoneNumber(account);
        accountDao.persist(account);
        setActionToken(account);

        emailMessageService.send(new AdministratorActivationMessageTemplate(account, applicationProperties));

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
        checkPhoneNumber(account);
        accountDao.persist(account);

        return account;
    }

    @RoleSecured(AccountRole.Administrator)
    public Account createPropertyOwner(@NotNull PersistPropertyOwnerRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail());

        final PropertyOwner account = this.getEntityInstance(request.getPrimaryEmail(), PropertyOwner.class);
        account.setRole(AccountRole.PropertyOwner);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setCreatedAt(new Date());
        checkPhoneNumber(account);
        accountDao.persist(account);
        setActionToken(account);

        emailMessageService.send(new PropertyOwnerActivationMessageTemplate(account, applicationProperties));

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
        checkPhoneNumber(account);
        propertyOwnerDao.persist(account);

        return account;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public PropertyManager createPropertyManager(@NotNull PersistPropertyManagerRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail());

        Property managedProperty;
        if ( authorizationManager.getCurrentAccount().getRole() == AccountRole.PropertyOwner ) {
            Objects.requireNonNull(request.getManagedPropertyId(), "Managed property id is null");

            managedProperty = propertyDao.findById(request.getManagedPropertyId());
            authorizationManager.checkOwner(managedProperty);
        }
        else {
            managedProperty = authorizationManager.getCurrentProperty();
        }

        final PropertyManager account = this.getEntityInstance(request.getPrimaryEmail(), PropertyManager.class);
        account.setRole(AccountRole.PropertyManager);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setCreatedAt(new Date());
        account.setManagedProperty(managedProperty);
        checkPhoneNumber(account);
        accountDao.persist(account);
        setActionToken(account);

        emailMessageService.send(new EmployeeActivationMessageTemplate(account, applicationProperties));

        return account;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public PropertyManager updatePropertyManager(@NotNull Long accountId, @NotNull PersistPropertyManagerRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        Property managedProperty;
        PropertyManager account;
        if ( authorizationManager.getCurrentAccount().getRole() == AccountRole.PropertyOwner ) {
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
        checkPhoneNumber(account);
        propertyManagerDao.persist(account);

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager})
    public SecurityEmployee createSecurityGuy(@NotNull PersistSecurityGuyRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail());

        final PropertyManager manager = (PropertyManager) authorizationManager.getCurrentAccount();

        final SecurityEmployee account = this.getEntityInstance(request.getPrimaryEmail(), SecurityEmployee.class);
        account.setRole(AccountRole.Security);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setCreatedAt(new Date());
        account.setManager(manager);
        checkPhoneNumber(account);
        securityEmployeeDao.persist(account);
        setActionToken(account);

        emailMessageService.send(new EmployeeActivationMessageTemplate(account, applicationProperties));

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.Security})
    public SecurityEmployee updateSecurityGuy(@NotNull Long accountId, @NotNull PersistSecurityGuyRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        final SecurityEmployee account = securityEmployeeDao.findById(accountId);
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.Security ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        checkPhoneNumber(account);
        securityEmployeeDao.persist(account);

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner})
    public MaintenanceEmployee createMaintenanceGuy(@NotNull PersistMaintenanceGuyRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail());

        final PropertyManager manager = (PropertyManager) authorizationManager.getCurrentAccount();

        final MaintenanceEmployee account = this.getEntityInstance(request.getPrimaryEmail(), MaintenanceEmployee.class);
        account.setRole(AccountRole.Maintenance);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setCreatedAt(new Date());
        account.setManager(manager);
        checkPhoneNumber(account);
        maintenanceEmployeeDao.persist(account);
        setActionToken(account);

        emailMessageService.send(new EmployeeActivationMessageTemplate(account, applicationProperties));

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.Maintenance})
    public MaintenanceEmployee updateMaintenanceGuy(@NotNull Long accountId, @NotNull PersistMaintenanceGuyRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        final MaintenanceEmployee account = maintenanceEmployeeDao.findById(accountId);
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.Maintenance ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        checkPhoneNumber(account);
        maintenanceEmployeeDao.persist(account);

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner})
    public AssistantPropertyManager createAssistantPropertyManager(@NotNull PersistAssistantPropertyManagerRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail());

        final Account currentAccount = authorizationManager.getCurrentAccount();
        final PropertyManager manager;
        if ( currentAccount instanceof PropertyManager ) {
            manager = (PropertyManager) currentAccount;
        }
        else if ( currentAccount instanceof PropertyOwner ) {
            Objects.requireNonNull(request.getManagerId(), "Manager is null");
            manager = propertyManagerDao.findById(request.getManagerId());
            if ( manager == null ) {
                throw new EntityNotFoundException(String.format("Property manager id=%d not found", request.getManagerId()));
            }
            authorizationManager.checkOwner(manager.getManagedProperty());
        }
        else {
            throw new SecurityException("Not allowed to perform action");
        }

        final AssistantPropertyManager account = this.getEntityInstance(request.getPrimaryEmail(), AssistantPropertyManager.class);
        account.setRole(AccountRole.AssistantPropertyManager);
        mapper.fillAccount(request, account);
        account.setActive(false);
        account.setCreatedAt(new Date());
        account.setManager(manager);
        checkPhoneNumber(account);
        assistantPropertyManagerDao.persist(account);
        setActionToken(account);

        emailMessageService.send(new EmployeeActivationMessageTemplate(account, applicationProperties));

        return account;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner})
    public AssistantPropertyManager updateAssistantPropertyManager(@NotNull Long accountId, @NotNull PersistAssistantPropertyManagerRequest request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(request);

        final AssistantPropertyManager account = assistantPropertyManagerDao.findById(accountId);
        preventAccountDuplicity(request.getPrimaryEmail(), account.getPrimaryEmail());
        if ( account.getRole() != AccountRole.AssistantPropertyManager ) {
            throw new SecurityException("Account role change is not allowed.");
        }
        mapper.fillAccount(request, account);
        checkPhoneNumber(account);
        assistantPropertyManagerDao.persist(account);

        return account;
    }

    public void resetPasswordFromRequest(@NotNull AskResetPasswordRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        Objects.requireNonNull(request);

        final Account account = getAccount(request.getEmail());
        setActionToken(account);
        accountDao.persist(account);
        emailMessageService.send(new ResetPasswordMessageTemplate(account, applicationProperties));
    }

    public void logout() {
        Object deviceObject = httpSession.getAttribute(this.deviceProperties.getSessionKeyDevice());
        if ( deviceObject instanceof Device ) {
            final Device device = deviceDao.findById(((Device) deviceObject).getId());
            device.setDeletedAt(new Date());
            deviceDao.persist(device);
        }
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public Account deleteAccount(@NotNull Long accountId) {
        Objects.requireNonNull(accountId);
        return deleteAccount(getAccount(accountId));
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    private Account deleteAccount(@NotNull Account account) {
        Objects.requireNonNull(account);
        if ( account instanceof Tenant ) {
            throw new IllegalArgumentException("Cannot delete tenant by using this API call");
        }

        authorizationManager.checkWrite(account);

        account.setDeletedAt(new Date());
        account.setActive(Boolean.FALSE);

        accountDao.persist(account);

        return account;
    }

    private void preventAccountDuplicity(@Nonnull String email) {
        preventAccountDuplicity(email, null);
    }

    private void preventAccountDuplicity(@Nonnull String email, String emailExisting) {
        if ( Objects.equals(email, emailExisting) ) {
            // email will not change, assume account is not a duplicate
            return;
        }

        if ( accountDao.findByEmail(email) != null ) {
            throw new IllegalArgumentException(String.format("Account %s already exists.", email));
        }
    }

    private void checkPhoneNumber(@Nonnull Account account) {
        if ( StringUtils.isNotBlank(account.getPrimaryPhone()) && !(smsMessageSender.validPhoneNumber(account.getPrimaryPhone())) ) {
            throw new ValidationException(String.format("Primary phone number %s is not valid.", account.getPrimaryPhone()));
        }
        if ( StringUtils.isNotBlank(account.getSecondaryPhone()) && !(smsMessageSender.validPhoneNumber(account.getSecondaryPhone())) ) {
            throw new ValidationException(String.format("Secondary phone number %s is not valid.", account.getSecondaryPhone()));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Account> T getEntityInstance(String email, Class<T> clazz) {
        Objects.requireNonNull(email, "Email must not be null");

        Account byEmail = accountDao.findByEmail(email);
        if ( byEmail == null ) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to create new account");
            }
        } else {
            if (byEmail.isDeleted() && ! byEmail.getActive()) {
                if (byEmail.getClass().isInstance(clazz)) {
                    byEmail.setActive(true);
                    byEmail.setDeletedAt(null);
                    return (T) byEmail;
                } else {
                    accountDao.delete(byEmail);

                    try {
                        return clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e2) {
                        throw new IllegalArgumentException("Unable to create new account");
                    }
                }
            } else {
                throw new IllegalArgumentException(String.format("Account %s already exists.", email));
            }
        }
    }


}