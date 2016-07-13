package com.creatix.security;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.enums.AccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * This class contains all authorization checks.
 */
@Component
public class AuthorizationManager {

    @Autowired
    private AccountDao accountDao;

    public boolean isSelf(Account account) {
        return Objects.equals(account, getCurrentAccount());
    }

    public boolean isAdministrator() {
        return getCurrentAccount().getRole() == AccountRole.Administrator;
    }

    public Account getCurrentAccount() throws SecurityException {
        return getCurrentAccount(false);
    }

    public boolean hasCurrentAccount() {
        return getCurrentAccount(true) != null;
    }

    private Account getCurrentAccount(boolean suppressException) throws SecurityException {

        Account current = null;

        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if ( securityContext != null ) {
            final Authentication authentication = securityContext.getAuthentication();
            if ( authentication != null ) {
                if ( authentication instanceof AuthenticatedUserDetails ) {
                    current = ((AuthenticatedUserDetails) authentication).getAccount();
                }
                else if ( authentication.getPrincipal() instanceof AuthenticatedUserDetails ) {
                    current = ((AuthenticatedUserDetails) authentication.getPrincipal()).getAccount();
                }
            }
        }

        if ( (current == null) && !(suppressException) ) {
            throw new SecurityException("No authenticated account found in session.");
        }

        return current != null ? accountDao.findById(current.getId()) : null;
    }

    public Property getCurrentProperty() throws SecurityException {
        Account account = getCurrentAccount(false);
        return getCurrentProperty(account);
    }

    public Property getCurrentProperty(Account account) throws SecurityException {
        assert account != null;

        switch ( account.getRole() ) {
            case Tenant:
                return ((Tenant) account).getApartment().getProperty();
            case PropertyManager:
                return ((PropertyManager) account).getManagedProperty();
            default:
                if ( account instanceof ManagedEmployee ) {
                    return ((ManagedEmployee) account).getManager().getManagedProperty();
                }
                else {
                    throw new SecurityException("Impossible to extract single linked apartment.");
                }
        }
    }

    public void checkManager(@NotNull Property property) {
        if ( !(isManager(property)) ) {
            throw new SecurityException("Not a apartment manager");
        }
    }

    @RoleSecured(AccountRole.PropertyManager)
    public boolean isManager(@NotNull Property property) {
        Objects.requireNonNull(property);
        return Objects.equals(property, ((PropertyManager) getCurrentAccount()).getManagedProperty());
    }

    public void checkAccess(@NotNull Property property) {
        Objects.requireNonNull(property);
        boolean allowed = false;
        switch ( getCurrentAccount().getRole() ) {
            case Administrator:
                allowed = true;
                break;
            case PropertyOwner:
                allowed = property.getOwner().equals(this.getCurrentAccount());
                break;
            case PropertyManager:
                //noinspection SuspiciousMethodCalls
                allowed = property.getManagers().contains(this.getCurrentAccount());
                break;
            case AssistantPropertyManager:
            case Security:
            case Maintenance:
                allowed = property.getManagers().contains(((ManagedEmployee) this.getCurrentAccount()).getManager());
                break;
            case Tenant:
                allowed = Objects.equals(property, ((Tenant) getCurrentAccount()).getApartment().getProperty());
                break;
        }
        if ( !(allowed) ) {
            throw new SecurityException(String.format("You are not eligible to read info about property with id=%d", property.getId()));
        }
    }

    public boolean checkAccess(@NotNull Apartment apartment) {
        Objects.requireNonNull(apartment);
        boolean allowed = false;
        switch ( this.getCurrentAccount().getRole() ) {
            case Administrator:
                allowed = true;
                break;
            case PropertyOwner:
                allowed = apartment.getProperty().getOwner().equals(this.getCurrentAccount());
                break;
            case PropertyManager:
                //noinspection SuspiciousMethodCalls
                allowed = apartment.getProperty().getManagers().contains(this.getCurrentAccount());
                break;
            case AssistantPropertyManager:
                allowed = apartment.getProperty().getManagers().contains(((ManagedEmployee) this.getCurrentAccount()).getManager());
                break;
        }
        if ( allowed ) {
            return allowed;
        }
        throw new SecurityException(String.format("You are not eligible to read info about apartment with id=%d", apartment.getId()));
    }

    public void checkOwner(Property property) {
        if ( !(isOwner(property)) ) {
            throw new SecurityException("Not owner of the property.");
        }
    }

    public boolean isOwner(@NotNull Property property) {
        Objects.requireNonNull(property);
        return Objects.equals(property.getOwner(), getCurrentAccount());
    }

    public boolean checkAccess(@NotNull Device device) {
        Objects.requireNonNull(device);

        if ( device.getAccount() == null ) {
            return true;
            //throw new SecurityException(String.format("You are not eligible to read unassigned device with id=%d", device.getId()));
        }

        if ( device.getAccount().getId().equals(this.getCurrentAccount().getId()) ) {
            return true;
        }

        throw new SecurityException(String.format("You are not eligible to read device with id=%d", device.getId()));
    }

    public boolean checkAccess(@NotNull Device device, @NotNull Account account) {
        Objects.requireNonNull(device);
        Objects.requireNonNull(account);

        return true;

        /*
        TODO: implementation will be specified in future
        if (device.getAccount() == null) {
            return true;
        }

        if (this.getCurrentAccount().getRole() == AccountRole.Administrator ||
                device.getAccount().getId().equals(account.getId())) {
            return true;
        }

        throw new SecurityException(String.format("You are not eligible to read device with id=%d", device.getId()));
        */
    }

    public boolean isEligibleToReadProperty(Property property) {
        final Account account = getCurrentAccount();
        switch ( account.getRole() ) {
            case Administrator:
                return true;
            case PropertyOwner:
                return property.getOwner().equals(account);
            case PropertyManager:
                //noinspection SuspiciousMethodCalls
                return property.getManagers().contains(account);
            case AssistantPropertyManager:
                return property.getManagers().contains(((ManagedEmployee) account).getManager());
            default:
                return false;
        }
    }

    // same as read accessibility except Administrator
    public boolean isEligibleToUpdateProperty(Property property) {
        final Account account = getCurrentAccount();
        return !account.getRole().equals(AccountRole.Administrator) && isEligibleToReadProperty(property);
    }

    public boolean canRead(@NotNull MaintenanceReservation reservation) {
        Objects.requireNonNull(reservation, "Maintenance reservation is null");

        final Account account = getCurrentAccount();
        if ( account instanceof Tenant ) {
            final Tenant tenant = (Tenant) account;
            if ( reservation.getNotification() != null ) {
                return Objects.equals(tenant, reservation.getNotification().getTargetApartment().getTenant());
            }
        }
        else if ( account instanceof MaintenanceEmployee ) {
            final MaintenanceEmployee maintenance = (MaintenanceEmployee) account;
            return Objects.equals(maintenance, reservation.getEmployee());
        }

        return false;
    }
}
