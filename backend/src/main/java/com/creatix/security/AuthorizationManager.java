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

    public boolean hasAnyOfRoles(AccountRole... roles) {
        for ( AccountRole role : roles ) {
            if ( getCurrentAccount().getRole() == role ) {
                return true;
            }
        }

        return false;
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

    public boolean isManager(@NotNull Property property) {
        Objects.requireNonNull(property, "Property is null");

        if ((getCurrentAccount() instanceof PropertyManager) && Objects.equals(property, ((PropertyManager) getCurrentAccount()).getManagedProperty())) {
            return true;
        }
        else if ( (getCurrentAccount() instanceof AssistantPropertyManager) && Objects.equals(property, ((AssistantPropertyManager) getCurrentAccount()).getManager().getManagedProperty()) ) {
            return true;
        }

        return false;
    }

    public void checkRead(@NotNull Property property) {
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
                allowed = property.getManagers().contains(((AssistantPropertyManager) this.getCurrentAccount()).getManager());
                break;
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
            return true;
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

    public boolean canWrite(Property property) {
        final Account account = getCurrentAccount();
        switch ( account.getRole() ) {
            case Administrator:
                return true;
            case PropertyOwner:
                return property.getOwner().equals(account);
            case PropertyManager:
                return property.getManagers().contains((PropertyManager) account);
            case AssistantPropertyManager:
                return property.getManagers().contains(((ManagedEmployee) account).getManager());
            default:
                return false;
        }
    }

    // same as read accessibility except Administrator
    public boolean canUpdateProperty(Property property) {
        final Account account = getCurrentAccount();
        return !account.getRole().equals(AccountRole.Administrator) && canWrite(property);
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

    public void checkWrite(@NotNull Tenant tenant) {
        Objects.requireNonNull(tenant);

        if ( !(canWrite(tenant)) ) {
            throw new SecurityException(String.format("Cannot modify tenant id=%d", tenant.getId()));
        }
    }

    private boolean canWrite(@NotNull Tenant tenant) {
        if ( isAdministrator() ) {
            return true;
        }
        if ( tenant.getApartment() == null ) {
            return hasAnyOfRoles(AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.Administrator, AccountRole.AssistantPropertyManager);
        }
        else {
            final Property property = tenant.getApartment().getProperty();
            return isManager(property) || isOwner(property) || hasAnyOfRoles(AccountRole.Administrator);
        }
    }

    public void checkWrite(@NotNull Account account) {
        Objects.requireNonNull(account);

        if ( !(canWrite(account)) ) {
            throw new SecurityException(String.format("Cannot modify account id=%d", account.getId()));
        }
    }

    private boolean canWrite(@NotNull Account account) {
        if ( account instanceof Tenant ) {
            return canWrite((Tenant) account);
        }
        else if ( account instanceof ManagedEmployee ) {
            ManagedEmployee employee = (ManagedEmployee) account;
            return canWrite(employee.getManager().getManagedProperty());
        }
        else if ( account instanceof AssistantPropertyManager ) {
            AssistantPropertyManager assistant = (AssistantPropertyManager) account;
            return canWrite(assistant.getManager().getManagedProperty());
        }
        else if ( account instanceof PropertyManager ) {
            return hasAnyOfRoles(AccountRole.Administrator, AccountRole.PropertyOwner);
        }
        else {
            return hasAnyOfRoles(AccountRole.Administrator);
        }
    }

    public boolean canModifyAllProfileInfo() {
        return hasAnyOfRoles(
                AccountRole.Administrator,
                AccountRole.PropertyOwner,
                AccountRole.PropertyManager,
                AccountRole.Security,
                AccountRole.Maintenance);
    }
}
