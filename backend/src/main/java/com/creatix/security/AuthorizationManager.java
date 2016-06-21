package com.creatix.security;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.entity.*;
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
        if (securityContext != null) {
            final Authentication authentication = securityContext.getAuthentication();
            if (authentication != null) {
                if (authentication instanceof AuthenticatedUserDetails) {
                    current = ((AuthenticatedUserDetails) authentication).getAccount();
                } else if (authentication.getPrincipal() instanceof AuthenticatedUserDetails) {
                    current = ((AuthenticatedUserDetails) authentication.getPrincipal()).getAccount();
                }
            }
        }

        if ((current == null) && !(suppressException)) {
            throw new SecurityException("No authenticated account found in session.");
        }

        return current != null ? accountDao.findById(current.getId()) : null;
    }

    public Property getCurrentProperty() throws SecurityException {
        Account account = getCurrentAccount(false);
        assert account != null;

        switch (account.getRole()) {
            case Tenant:
                return ((Tenant) account).getApartment().getProperty();
            case PropertyManager:
                return ((PropertyManager) account).getManagedProperty();
            default:
                if (account instanceof Employee)
                    return ((Employee) account).getManager().getManagedProperty();
                else
                    throw new SecurityException("Impossible to extract single linked property.");
        }
    }

    public void checkManager(@NotNull Property property) {
        if ( !(isManager(property)) ) {
            throw new SecurityException("Not a property manager");
        }
    }

    @RoleSecured(AccountRole.PropertyManager)
    public boolean isManager(@NotNull Property property) {
        Objects.requireNonNull(property);
        return Objects.equals(property, ((PropertyManager) getCurrentAccount()).getManagedProperty());
    }

}
