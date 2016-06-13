package com.creatix.security;

import com.creatix.domain.entity.Account;
import com.creatix.domain.enums.AccountRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * This class contains all authorization checks.
 */
@Component
public class AuthorizationManager {

    public boolean isSelf(Account account) {
        return Objects.equals(account, getCurrentAccount());
    }

    public boolean isAdministrator() {
        return getCurrentAccount().getRole() == AccountRole.PropertyOwner;
    }

    public Account getCurrentAccount() throws SecurityException {
        return getCurrentAccount(false);
    }

    public boolean hasCurrentAccount() {
        return getCurrentAccount(true) != null;
    }

    private Account getCurrentAccount(boolean suppressException) throws SecurityException{

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

        return current;
    }

}
