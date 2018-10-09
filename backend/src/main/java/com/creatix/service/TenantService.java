package com.creatix.service;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.Mapper;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.tenant.PersistTenantRequest;
import com.creatix.domain.dto.tenant.subs.CreateSubTenantRequest;
import com.creatix.domain.dto.tenant.subs.UpdateSubTenantRequest;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.ParkingStall;
import com.creatix.domain.entity.store.Vehicle;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.email.SubTenantActivationMessageTemplate;
import com.creatix.message.template.email.TenantActivationMessageTemplate;
import com.creatix.message.template.sms.ActivationMessageTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.BitlyService;
import com.creatix.service.message.EmailMessageService;
import freemarker.template.TemplateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TenantService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TenantDao tenantDao;
    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private SubTenantDao subTenantDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private Mapper mapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private EmailMessageService emailMessageService;
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private SmsMessageSender smsMessageSender;
    @Autowired
    private BitlyService bitlyService;

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private @Nonnull <T, ID> T getOrElseThrow(@Nonnull ID id, @Nonnull DaoBase<T, ID> dao, @Nonnull EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public @Nonnull Tenant createTenantFromRequest(@Nonnull PersistTenantRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        Objects.requireNonNull(request);
        preventAccountDuplicity(request.getPrimaryEmail(), null);

        final Apartment apartment = getOrElseThrow(request.getApartmentId(), apartmentDao,
                new EntityNotFoundException(String.format("Apartment with id %d not found", request.getApartmentId())));

        if (authorizationManager.getCurrentAccount().getRole() != AccountRole.Administrator) {
            authorizationManager.checkManager(apartment.getProperty());
        }

        if ( apartment.getTenant() != null ) {
            throw new IllegalArgumentException(String.format("Apartment id=%d has already tenant id=%d assigned.", apartment.getId(), apartment.getTenant().getId()));
        }

        Tenant tenant = null;
        final Account account = accountDao.findByEmail(request.getPrimaryEmail());
        if ( account instanceof Tenant ) {
            tenant = (Tenant) account;
        }
        else if ( account != null ) {
            throw new IllegalArgumentException(String.format("Account with email=%s already exists", request.getPrimaryEmail()));
        }

        if ( tenant == null ) {
            tenant = mapper.toTenant(request);
        }
        else {
            if ( tenant.getActive() == Boolean.TRUE ) {
                throw new IllegalArgumentException(String.format("Tenant with email=%s already exists", request.getPrimaryEmail()));
            }
            mapper.fillTenant(request, tenant);
        }

        if (request.getIsNeighborhoodNotificationEnable() != null) {
            tenant.setIsNeighborhoodNotificationEnable(request.getIsNeighborhoodNotificationEnable());
        } else {
            tenant.setIsNeighborhoodNotificationEnable(true);
        }

        tenant.setApartment(apartment);
        tenant.setActive(false);
        tenant.setDeletedAt(null);
        tenant.setRole(AccountRole.Tenant);
        tenant.setEnableSms(true);
        tenantDao.persist(tenant);
        apartment.setTenant(tenant);
        apartmentDao.persist(apartment);

        accountService.setActionToken(tenant);

        emailMessageService.send(new TenantActivationMessageTemplate(tenant, applicationProperties));

        if (apartment.getProperty().getEnableSms()) {
            String shortUrl = bitlyService.getShortUrl(applicationProperties.buildAdminUrl(String.format("new-user/%s", account.getActionToken())).toString());
            logger.debug("Generated short url for sms activation account. Url: " + shortUrl);
            try {
                smsMessageSender.send(new ActivationMessageTemplate(shortUrl, account.getPrimaryPhone()));
            } catch (Exception e) {
                logger.error("There is problem with smsMessageSender.send in tenantService: " + e);
            }
        }

        return tenant;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager, AccountRole.Tenant})
    public @Nonnull Tenant updateTenantFromRequest(long tenantId, @Nonnull PersistTenantRequest request) {
        Objects.requireNonNull(request);

        final Apartment apartment = getOrElseThrow(request.getApartmentId(), apartmentDao,
                new EntityNotFoundException(String.format("Apartment id=%d not found", request.getApartmentId())));

        if (!AccountRole.Tenant.equals(authorizationManager.getCurrentAccount().getRole())) {
            authorizationManager.checkManager(apartment.getProperty());
        }

        if (AccountRole.Tenant.equals(authorizationManager.getCurrentAccount().getRole()) && authorizationManager.getCurrentAccount().getId() != tenantId) {
            throw new AccessDeniedException("Tenant can update only himself.");
        }

        final Tenant tenant = getTenant(tenantId);
        preventAccountDuplicity(request.getPrimaryEmail(), tenant.getPrimaryEmail());

        if ( (apartment.getTenant() != null) && !(Objects.equals(apartment.getTenant(), tenant)) ) {
            throw new IllegalArgumentException(String.format("Apartment id=%d has already tenant id=%d assigned.", apartment.getId(), apartment.getTenant().getId()));
        }

        mapper.fillTenant(request, tenant);

        if ( (tenant.getApartment() != null) && !(Objects.equals(apartment, tenant.getApartment())) ) {
            // apartment changed, un-assign tenant from previous apartment
            final Apartment apartmentPrev = tenant.getApartment();
            apartmentPrev.setTenant(null);
            apartmentDao.persist(apartmentPrev);
        }

        if (request.getIsNeighborhoodNotificationEnable() != null) {
            tenant.setIsNeighborhoodNotificationEnable(request.getIsNeighborhoodNotificationEnable());
        }

        tenant.setApartment(apartment);
        tenantDao.persist(tenant);
        apartment.setTenant(tenant);
        apartmentDao.persist(apartment);

        return tenant;
    }

    @RoleSecured
    public @Nonnull Tenant getTenant(@Nonnull Long tenantId) {
        return getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
    }

    @RoleSecured
    public @Nonnull List<Vehicle> getTenantVehicles(@Nonnull Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return new ArrayList<>(tenant.getVehicles());
    }

    @RoleSecured
    public @Nonnull List<ParkingStall> getTenantParkingStalls(@Nonnull Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return new ArrayList<>(tenant.getParkingStalls());
    }

    @RoleSecured
    public @Nonnull List<SubTenant> getSubTenants(@Nonnull Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return new ArrayList<>(tenant.getSubTenants());
    }

    @RoleSecured({AccountRole.Tenant})
    public @Nonnull SubTenant createSubTenant(@Nonnull CreateSubTenantRequest request) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        return createSubTenant(authorizationManager.getCurrentAccount().getId(), request);
    }

    @RoleSecured({AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public @Nonnull SubTenant createSubTenant(@Nonnull Long tenantId, @Nonnull CreateSubTenantRequest request) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        Objects.requireNonNull(request);

        preventAccountDuplicity(request.getPrimaryEmail(), null);

        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if ( authorizationManager.isSelf(tenant) || authorizationManager.hasAnyOfRoles(AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager) ) {
            final SubTenant subTenant = mapper.toSubTenant(request);
            subTenant.setRole(AccountRole.SubTenant);
            subTenant.setActive(false);
            accountService.setActionToken(subTenant);
            subTenant.setParentTenant(tenant);
            subTenantDao.persist(subTenant);

            emailMessageService.send(new SubTenantActivationMessageTemplate(subTenant, applicationProperties));

            return subTenant;
        }

        throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
    }

    @RoleSecured
    public SubTenant getSubTenant(@Nonnull Long subTenantId) {
        return getOrElseThrow(subTenantId, subTenantDao, new EntityNotFoundException(String.format("Sub-tenant id=%d not found", subTenantId)));
    }

    @RoleSecured({AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public @Nonnull SubTenant updateSubTenant(@Nonnull Long subTenantId, @NotNull UpdateSubTenantRequest request) {
        Objects.requireNonNull(request);

        final SubTenant subTenant = getOrElseThrow(subTenantId, subTenantDao, new EntityNotFoundException(String.format("Sub-tenant id=%d not found", subTenantId)));
        if ( authorizationManager.isSelf(subTenant.getParentTenant()) || authorizationManager.hasAnyOfRoles(AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager) ) {
            mapper.fillSubTenant(request, subTenant);
            subTenantDao.persist(subTenant);
            return subTenant;
        }
        else {
            throw new SecurityException(String.format("You are not eligible to update subtenant=%d profile", subTenantId));
        }
    }

    @RoleSecured({AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public void deleteSubTenant(@Nonnull Long subTenantId) {
        final SubTenant subTenant = getOrElseThrow(subTenantId, subTenantDao, new EntityNotFoundException(String.format("Sub-tenant id=%d not found", subTenantId)));
        if ( authorizationManager.isSelf(subTenant.getParentTenant()) || authorizationManager.hasAnyOfRoles(AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager) ) {
            subTenant.setDeletedAt(new Date());
            subTenant.setActive(Boolean.FALSE);
            subTenant.setParentTenant(null);
            subTenantDao.persist(subTenant);
        }
        else {
            throw new SecurityException(String.format("You are not eligible to delete subtenant=%d profile", subTenantId));
        }
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public @Nonnull Tenant deleteTenant(@Nonnull Long tenantId) {
        return deleteTenant(getTenant(tenantId));
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    private @Nonnull Tenant deleteTenant(@Nonnull Tenant tenant) {
        Objects.requireNonNull(tenant);
        authorizationManager.checkWrite(tenant);

        if ( tenant.getApartment() != null ) {
            final Apartment apartment = tenant.getApartment();
            apartment.setTenant(null);
            apartmentDao.persist(apartment);
        }

        tenant.setDeletedAt(new Date());
        tenant.setActive(Boolean.FALSE);
        tenant.setApartment(null);
        tenantDao.persist(tenant);

        // delete subtenants
        for ( SubTenant subTenant : tenant.getSubTenants() ) {
            subTenant.setDeletedAt(new Date());
            subTenant.setActive(Boolean.FALSE);
            subTenant.setParentTenant(null);
            subTenantDao.persist(subTenant);
        }

        return tenant;
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
}
