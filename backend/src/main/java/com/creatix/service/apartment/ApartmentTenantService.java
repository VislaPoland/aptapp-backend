package com.creatix.service.apartment;

import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dao.TenantBaseDao;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.account.Tenant;
import com.creatix.domain.entity.account.TenantBase;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Service
@Transactional
public class ApartmentTenantService {

    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private TenantBaseDao tenantBaseDao;
    @Autowired
    private AuthorizationManager authorizationManager;

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public TenantBase delete(@NotNull Long apartmentId, @NotNull Long tenantId) {
        Objects.requireNonNull(apartmentId);

        final Apartment apartment = this.getApartment(apartmentId);
        this.authorizationManager.checkAccess(apartment);

        final TenantBase tenant = this.getTenant(apartmentId, tenantId);
        if (tenant.isDeleted() == false) {
            Date now = new Date();
            tenant.setDeletedAt(now);
            if (tenant instanceof Tenant) {
                ((Tenant) tenant).getSubTenants().stream().forEach( subTenant -> {
                    if (subTenant.isDeleted() == false) {
                        subTenant.setDeletedAt(now);
                    }
                });
            }
            this.tenantBaseDao.persist(tenant);
        }

        return tenant;
    }

    public Apartment getApartment(@NotNull Long apartmentId) {
        Objects.requireNonNull(apartmentId);

        final Apartment apartment = this.apartmentDao.findById(apartmentId);
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment with id %d not found", apartmentId));
        }

        return apartment;
    }

    private TenantBase getTenant(@NotNull Long apartmentId, @NotNull Long employeeId) {
        Objects.requireNonNull(apartmentId);
        Objects.requireNonNull(employeeId);

        final TenantBase contact = this.tenantBaseDao.findById(apartmentId, employeeId);

        if ( contact == null ) {
            throw new EntityNotFoundException(String.format("Tenant id=%d not found", employeeId));
        }

        return contact;
    }

}
