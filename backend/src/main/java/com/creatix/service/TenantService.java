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
import com.creatix.message.template.email.SubTenantActivationMessageTemplate;
import com.creatix.message.template.email.TenantActivationMessageTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.EmailMessageService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private VehicleDao vehicleDao;
    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private ParkingStallDao parkingStallDao;
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

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public Tenant createTenantFromRequest(@NotNull PersistTenantRequest request) throws MessageDeliveryException, TemplateException, IOException, MessagingException {
        Objects.requireNonNull(request);

        final Apartment apartment = getOrElseThrow(request.getApartmentId(), apartmentDao,
                new EntityNotFoundException(String.format("Apartment with id %d not found", request.getApartmentId())));
        authorizationManager.checkManager(apartment.getProperty());

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

        return tenant;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public Tenant updateTenantFromRequest(long tenantId, @NotNull PersistTenantRequest request) {
        Objects.requireNonNull(request);

        final Apartment apartment = getOrElseThrow(request.getApartmentId(), apartmentDao,
                new EntityNotFoundException(String.format("Apartment id=%d not found", request.getApartmentId())));
        authorizationManager.checkManager(apartment.getProperty());

        final Tenant tenant = getTenant(tenantId);

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
        tenant.setApartment(apartment);
        tenantDao.persist(tenant);
        apartment.setTenant(tenant);
        apartmentDao.persist(apartment);

        return tenant;
    }

    @RoleSecured
    public Tenant getTenant(Long tenantId) {
        return getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
    }

    @RoleSecured
    public List<Vehicle> getTenantVehicles(Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return new ArrayList<>(tenant.getVehicles());
    }

    @RoleSecured
    public List<ParkingStall> getTenantParkingStalls(Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return new ArrayList<>(tenant.getParkingStalls());
    }

    @RoleSecured
    public List<SubTenant> getSubTenants(Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return new ArrayList<>(tenant.getSubTenants());
    }

    @RoleSecured({AccountRole.Tenant})
    public SubTenant createSubTenant(@NotNull CreateSubTenantRequest request) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        return createSubTenant(authorizationManager.getCurrentAccount().getId(), request);
    }

    @RoleSecured({AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public SubTenant createSubTenant(Long tenantId, @NotNull CreateSubTenantRequest request) throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        Objects.requireNonNull(request);

        preventAccountDuplicity(request.getPrimaryEmail(), null);

        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if ( authorizationManager.isSelf(tenant) || authorizationManager.hasAnyOfRoles(AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager) ) {
            final SubTenant subTenant = mapper.toSubTenant(request);
            subTenant.setCompanyName(request.getCompanyName());
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
    public SubTenant getSubTenant(Long subTenantId) {
        return getOrElseThrow(subTenantId, subTenantDao, new EntityNotFoundException(String.format("Sub-tenant id=%d not found", subTenantId)));
    }

    @RoleSecured({AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager})
    public SubTenant updateSubTenant(Long subTenantId, @NotNull UpdateSubTenantRequest request) {
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
    public void deleteSubTenant(Long subTenantId) {
        final SubTenant subTenant = getOrElseThrow(subTenantId, subTenantDao, new EntityNotFoundException(String.format("Sub-tenant id=%d not found", subTenantId)));
        if ( authorizationManager.isSelf(subTenant.getParentTenant()) || authorizationManager.hasAnyOfRoles(AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator, AccountRole.AssistantPropertyManager) ) {
            subTenantDao.delete(subTenant);
        }
        else {
            throw new SecurityException(String.format("You are not eligible to delete subtenant=%d profile", subTenantId));
        }
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public Tenant deleteTenant(@NotNull Long tenantId) {
        return deleteTenant(getTenant(tenantId));
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public Tenant deleteTenant(@NotNull Tenant tenant) {
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

        return tenant;
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
