package com.creatix.security;

import com.creatix.domain.entity.Account;
import com.creatix.domain.entity.Gym;
import com.creatix.domain.entity.Trainer;
import com.creatix.domain.enums.Role;
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

    public void checkActive() {
        if ( !(getCurrentAccount().isActive()) ) {
            throw new SecurityException("Account is not activated");
        }
    }

    public void checkSelf(Trainer trainer) {
        checkSelf(trainer.getAccount());
    }

    public void checkSelf(Account account) {
        if ( !isSelf(account) ) {
            throw new SecurityException("Not owner of the account");
        }
    }

    public boolean isSelf(Account account) {
        return Objects.equals(account, getCurrentAccount());
    }

    public void checkManager(Gym gym) {
        if ( !isManager(gym) ) {
            throw new SecurityException("Not a gym manager");
        }
    }

    public boolean isManager(Gym gym) {
        return gym != null && Objects.equals(gym.getManager(), getCurrentAccount());
    }

    public void checkAdministrator() {
        if ( !isAdministrator() ) {
            throw new SecurityException();
        }
    }

    public boolean isAdministrator() {
        return getCurrentAccount().getRole() == Role.Administrator;
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

    public void checkTrainer() {
        if ( !isTrainer() ) {
            throw new SecurityException("Not a trainer");
        }
    }

    public boolean isTrainer() {
        final Account account = getCurrentAccount();
        return ((account.getRole() == Role.Trainer) && (account.getTrainer() != null));
    }
}
