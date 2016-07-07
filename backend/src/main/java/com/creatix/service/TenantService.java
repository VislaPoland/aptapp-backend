package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.tenant.CreateTenantRequest;
import com.creatix.domain.dto.tenant.TenantSelfUpdateRequest;
import com.creatix.domain.dto.tenant.UpdateTenantRequest;
import com.creatix.domain.dto.tenant.subs.CreateSubTenantRequest;
import com.creatix.domain.dto.tenant.subs.UpdateSubTenantRequest;
import com.creatix.domain.dto.tenant.vehicle.AssignVehicleRequest;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.ParkingStall;
import com.creatix.domain.entity.store.Vehicle;
import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.TenantType;
import com.creatix.message.EmailMessageSender;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.template.ActivationMessageTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class TenantService {
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private Mapper mapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private EmailMessageSender emailMessageSender;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if (item == null) {
            throw ex;
        }
        return item;
    }

    @RoleSecured(AccountRole.PropertyManager)
    public Tenant createTenantFromRequest(@NotNull CreateTenantRequest request) throws MessageDeliveryException, TemplateException, IOException {
        Objects.requireNonNull(request);

        final Apartment apartment = getOrElseThrow(request.getApartmentId(), apartmentDao,
                new EntityNotFoundException(String.format("Apartment with id %d not found", request.getApartmentId())));
        authorizationManager.checkManager(apartment.getProperty());

        if ( apartment.getTenant() != null ) {
            throw new IllegalArgumentException(String.format("Apartment id=%d has already tenant id=%d assigned.", apartment.getId(), apartment.getTenant().getId()));
        }

        final Tenant tenant = mapper.toTenant(request);
        tenant.setApartment(apartment);
        tenant.setActive(false);
        tenant.setRole(AccountRole.Tenant);
        tenantDao.persist(tenant);
        apartment.setTenant(tenant);
        apartmentDao.persist(apartment);

        accountService.setActionToken(tenant);

        emailMessageSender.send(new ActivationMessageTemplate(tenant));

        return tenant;
    }

    @RoleSecured(AccountRole.PropertyManager)
    public Tenant updateTenantFromRequest(long tenantId, @NotNull UpdateTenantRequest request) {
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
        return tenant.getVehicles().stream().collect(Collectors.toList());
    }

    @RoleSecured({AccountRole.Tenant})
    public Tenant updateTenantFromRequest(Long tenantId, @NotNull TenantSelfUpdateRequest request) {
        Objects.requireNonNull(request);

        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if (authorizationManager.isSelf(tenant)) {
            mapper.fillTenant(request, tenant);
            tenant.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            tenantDao.persist(tenant);

            return tenant;
        }

        throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
    }

    @RoleSecured
    public List<ParkingStall> getTenantParkingStalls(Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return tenant.getParkingStalls().stream().collect(Collectors.toList());
    }

    @RoleSecured({AccountRole.Tenant})
    public ParkingStall updateVehicleAtParkingStall(Long tenantId, Long parkingStallId, @NotNull AssignVehicleRequest request) {
        Objects.requireNonNull(request);

        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if (authorizationManager.isSelf(tenant)) {
            final ParkingStall parkingStall = parkingStallDao.findById(parkingStallId);
            if (tenant.getParkingStalls().contains(parkingStall)) {
                //TODO refactor this code to make it more readable and clean
                Vehicle vehicle = (parkingStall.getParkingVehicle() == null)? new Vehicle() : parkingStall.getParkingVehicle();
                mapper.fillVehicle(request, vehicle);
                vehicle.setParkingStall(parkingStall);
                vehicle.setOwner(tenant);

                parkingStall.setParkingVehicle(vehicle);
                parkingStallDao.persist(parkingStall);
                return parkingStall;
            }
            throw new SecurityException(String.format("You are not eligible to use parking stall=%d", parkingStallId));
        }
        throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
    }

    @RoleSecured({AccountRole.Tenant})
    public void deleteVehicle(Long tenantId, Long id) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));

        if (authorizationManager.isSelf(tenant)) {
            final Vehicle vehicle = getOrElseThrow(id, vehicleDao, new EntityNotFoundException(String.format("Vehicle id=%s not found", id)));
            final ParkingStall parkingStall = vehicle.getParkingStall();
            parkingStall.setParkingVehicle(null);
            parkingStallDao.persist(parkingStall);
        } else {
            throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
        }
    }

    @RoleSecured
    public List<SubTenant> getSubTenants(Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return tenant.getSubTenants().stream().collect(Collectors.toList());
    }

    @RoleSecured({AccountRole.Tenant})
    public SubTenant createSubTenant(Long tenantId, @NotNull CreateSubTenantRequest request) {
        Objects.requireNonNull(request);

        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if (authorizationManager.isSelf(tenant)) {
            final SubTenant subTenant = mapper.toSubTenant(request);
            subTenant.setCompanyName(tenant.getCompanyName());
            subTenant.setRole(AccountRole.SubTenant);
            subTenant.setActive(false);
            accountService.setActionToken(subTenant);
            subTenant.setType(TenantType.Sub);
            subTenant.setParentTenant(tenant);
            subTenantDao.persist(subTenant);
            return subTenant;
        }
        throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
    }

    @RoleSecured
    public SubTenant getSubTenant(Long subTenantId) {
        return getOrElseThrow(subTenantId, subTenantDao, new EntityNotFoundException(String.format("Sub-tenant id=%d not found", subTenantId)));
    }

    @RoleSecured({AccountRole.Tenant})
    public SubTenant updateSubTenant(Long tenantId, Long subTenantId, @NotNull UpdateSubTenantRequest request) {
        Objects.requireNonNull(request);

        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if (authorizationManager.isSelf(tenant)) {
            final SubTenant subTenant = getOrElseThrow(subTenantId, subTenantDao, new EntityNotFoundException(String.format("Sub-tenant id=%d not found", subTenantId)));

            if (tenant.getSubTenants().contains(subTenant)) {
                mapper.fillSubTenant(request, subTenant);
                subTenantDao.persist(subTenant);
                return subTenant;
            }
            throw new SecurityException(String.format("You are not eligible to edit sub-tenant=%d profile", subTenantId));
        }
        throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
    }

    @RoleSecured({AccountRole.Tenant})
    public void deleteSubTenant(Long tenantId, Long subTenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if (authorizationManager.isSelf(tenant)) {
            final SubTenant subTenant = getOrElseThrow(subTenantId, subTenantDao, new EntityNotFoundException(String.format("Sub-tenant id=%d not found", subTenantId)));

            if (tenant.getSubTenants().contains(subTenant)) {
                subTenantDao.delete(subTenant);
            } else {
                throw new SecurityException(String.format("You are not eligible to edit sub-tenant=%d profile", subTenantId));
            }
        } else {
            throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
        }
    }
}
