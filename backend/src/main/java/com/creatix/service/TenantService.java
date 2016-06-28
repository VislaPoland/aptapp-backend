package com.creatix.service;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.tenant.CreateTenantRequest;
import com.creatix.domain.dto.tenant.TenantSelfUpdateRequest;
import com.creatix.domain.dto.tenant.UpdateTenantRequest;
import com.creatix.domain.dto.tenant.vehicle.CreateVehicleRequest;
import com.creatix.domain.dto.tenant.vehicle.UpdateVehicleRequest;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.ParkingStall;
import com.creatix.domain.entity.Tenant;
import com.creatix.domain.entity.Vehicle;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private Mapper mapper;
    @Autowired
    private AccountService accountService;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if (item == null) {
            throw ex;
        }
        return item;
    }

    @RoleSecured(AccountRole.PropertyManager)
    public Tenant createTenantFromRequest(@NotNull CreateTenantRequest request) {
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
    public Vehicle createVehicleFromRequest(Long tenantId, @NotNull CreateVehicleRequest request) {
        Objects.requireNonNull(request);

        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if (authorizationManager.isSelf(tenant)) {
            final Vehicle vehicle = mapper.toVehicle(request);
            vehicle.setOwner(tenant);
            final ParkingStall parkingStall = getOrElseThrow(request.getParkingStallId(), parkingStallDao,
                    new EntityNotFoundException(String.format("Parking stall id=%d not found", request.getParkingStallId())));
            if (isTenantEligibleToUseParkingStall(tenant, parkingStall)) {
                vehicle.setParkingStall(parkingStall);
                vehicleDao.persist(vehicle);
                return vehicle;
            }

            throw new SecurityException(String.format("You are not eligible to use parking stall=%d", request.getParkingStallId()));
        }

        throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
    }

    private boolean isTenantEligibleToUseParkingStall(Tenant tenant, ParkingStall parkingStall) {
        return tenant.getParkingStalls().contains(parkingStall);
    }

    @RoleSecured({AccountRole.Tenant})
    public Vehicle updateVehicleFromRequest(Long tenantId, String licensePlate, @NotNull UpdateVehicleRequest request) {
        Objects.requireNonNull(request);

        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        if (authorizationManager.isSelf(tenant)) {
            final Vehicle vehicle = getOrElseThrow(licensePlate, vehicleDao, new EntityNotFoundException(String.format("Vehicle %s not found", licensePlate)));
            mapper.fillVehicle(request, vehicle);
            final ParkingStall parkingStall = getOrElseThrow(request.getParkingStallId(), parkingStallDao,
                    new EntityNotFoundException(String.format("Parking stall id=%d not found", request.getParkingStallId())));
            if (isTenantEligibleToUseParkingStall(tenant, parkingStall)) {
                vehicle.setParkingStall(parkingStall);
                vehicleDao.persist(vehicle);
                return vehicle;
            }

            throw new SecurityException(String.format("You are not eligible to use parking stall=%d", request.getParkingStallId()));
        }

        throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
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

    @RoleSecured({AccountRole.Tenant})
    public void deleteVehicle(Long tenantId, String licensePlate) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));

        if (authorizationManager.isSelf(tenant)) {
            final Vehicle vehicle = getOrElseThrow(licensePlate, vehicleDao, new EntityNotFoundException(String.format("Vehicle %s not found", licensePlate)));
            vehicleDao.delete(vehicle);
        } else {
            throw new SecurityException(String.format("You are not eligible to edit user=%d profile", tenantId));
        }
    }

    @RoleSecured
    public List<ParkingStall> getTenantParkingStalls(Long tenantId) {
        final Tenant tenant = getOrElseThrow(tenantId, tenantDao, new EntityNotFoundException(String.format("Tenant id=%d not found", tenantId)));
        return tenant.getParkingStalls().stream().collect(Collectors.toList());
    }
}
