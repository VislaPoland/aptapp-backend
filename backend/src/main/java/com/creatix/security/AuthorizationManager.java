package com.creatix.security;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.enums.AccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
            case SubTenant:
                return ((SubTenant) account).getApartment().getProperty();
            case PropertyManager:
                return ((PropertyManager) account).getManagedProperty();
            case AssistantPropertyManager:
                return ((AssistantPropertyManager) account).getManager().getManagedProperty();
            default:
                if ( account instanceof ManagedEmployee ) {
                    return ((ManagedEmployee) account).getManager().getManagedProperty();
                }
                else {
                    throw new SecurityException("Impossible to extract single linked property.");
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

    public void checkRead(@NotNull Property property) throws SecurityException {
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
            case SubTenant:
                allowed = Objects.equals(property, ((SubTenant) getCurrentAccount()).getApartment().getProperty());
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
                return property.getManagers().contains(((AssistantPropertyManager) account).getManager());
            default:
                return false;
        }
    }

    public void checkWrite(@NotNull Property property) {
        Objects.requireNonNull(property, "property");
        if ( !(canWrite(property)) ) {
            throw new SecurityException(String.format("Not allowed to modify property id=%d", property.getId()));
        }
    }

    public boolean canRead(Property property) {
        try {
            this.checkRead(property);
            return true;
        } catch (SecurityException e) {
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
                return Objects.equals(tenant, reservation.getNotification().getAuthor());
            }
        }
        else if ( account instanceof EmployeeBase ) {
            final Property property = getCurrentProperty(account);
            return Objects.equals(property, reservation.getSlot().getProperty());
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

    public boolean canResetActivationCode(@NotNull Account account) {
        Objects.requireNonNull(account, "Account is null");

        final Property property = getCurrentProperty(account);

        switch ( getCurrentAccount().getRole() ) {
            case Administrator:
                return true;
            case PropertyOwner:
                return isOwner(property);
            case PropertyManager:
            case AssistantPropertyManager:
                return isManager(property);
            default:
                return false;
        }
    }

    public void checkResetActivationCode(@NotNull Account account) {
        Objects.requireNonNull(account, "Account is null");

        if ( !(canResetActivationCode(account)) ) {
            throw new SecurityException(String.format("You are not allowed to reset user=%s activation code", account.getPrimaryEmail()));
        }
    }

    /**
     * Check access permissions
     *
     * <ol>
     *     <li>Tenant, Sub-Tenant - can modify only own items</li>
     *     <li>PropertyOwner, PropertyManager, AssistantPropertyManager - can modify all items linked to managed property</li>
     *     <li>Administrator - can modify everything</li>
     *     <li>Maintenance, Security - has no power here</li>
     * </ol>
     *
     * @param authorAccountId author of the item (either comment or community board post item)
     * @param property to which the item is linked
     * @param sessionAccount of user making request
     * @throws SecurityException if user is not authorized to make changes
     */
    public void checkCommunityBoardModifyAccess(Long authorAccountId, Property property, Account sessionAccount) throws SecurityException {

        switch (sessionAccount.getRole()) {
            case Tenant:
            case SubTenant:
                if (!authorAccountId.equals(sessionAccount.getId())) {
                    throw new SecurityException("You can not modify this item");
                }
                break;
            case PropertyOwner:
            case PropertyManager:
            case AssistantPropertyManager:
                if (!this.canWrite(property)) {
                    throw new SecurityException("You can not modify this item");
                }
                break;
            case Maintenance:
            case Security:
                throw new SecurityException("You can not modify this item");
            case Administrator:
                // he can do anything
                break;
        }
    }


    public @NotNull Set<Property> getAccountProperties(Account account) {
        switch (account.getRole()) {
            case Administrator:
                return Collections.emptySet();
            case PropertyOwner:
                return ((PropertyOwner) account).getOwnedProperties();
            case PropertyManager:
                return Collections.singleton(((PropertyManager) account).getManagedProperty());
            case AssistantPropertyManager:
                return Collections.singleton(((AssistantPropertyManager) account).getManager().getManagedProperty());
            case Maintenance:
                return Collections.singleton(((MaintenanceEmployee) account).getManager().getManagedProperty());
            case Security:
                return Collections.singleton(((SecurityEmployee) account).getManager().getManagedProperty());
            case Tenant:
                return Collections.singleton(((Tenant)account).getApartment().getProperty());
            case SubTenant:
                return Collections.singleton(((SubTenant)account).getApartment().getProperty());
            default:
                return Collections.emptySet();
        }
    }

    private Set<Property> getIntersection(Set<Property> set1, Set<Property> set2) {
        if (set1 == null || set2 == null) {
            return Collections.emptySet();
        }
        return set1.stream().filter(set2::contains).collect(Collectors.toSet());
    }

    /**
     * Account 1 is eligible to sent message to account 2 in case:
     * <ol>
     *     <li>source account is administrator</li>
     *     <li>source account is {@link AccountRole#PropertyOwner} or {@link AccountRole#PropertyManager}, or
     *      {@link AccountRole#AssistantPropertyManager} and have at least one property in common</li>
     *     <li>source account is either {@link AccountRole#Tenant} or {@link AccountRole#SubTenant} and destination is
     *     either {@link AccountRole#PropertyOwner}, {@link AccountRole#PropertyManager} or
     *      {@link AccountRole#AssistantPropertyManager}</li>
     * </ol>
     *
     * @param fromAccount
     * @param toAccount
     * @return
     */
    public boolean canSendMessage(Account fromAccount, Account toAccount){
        switch (fromAccount.getRole()) {
            case Administrator:
                return true;
            case PropertyOwner:
            case PropertyManager:
            case AssistantPropertyManager:
                return getIntersection(getAccountProperties(fromAccount), getAccountProperties(toAccount)).size() > 0;
            case Tenant:
            case SubTenant:
                return getIntersection(getAccountProperties(fromAccount), getAccountProperties(toAccount)).size() > 0 && (
                        toAccount.getRole() == AccountRole.PropertyOwner ||
                                toAccount.getRole() == AccountRole.PropertyManager ||
                                toAccount.getRole() == AccountRole.AssistantPropertyManager
                );
            default:
                return false;
        }
    }

    public boolean canSendMessageTo(Account toAccount){
        return canSendMessage(getCurrentAccount(), toAccount);
    }
}
