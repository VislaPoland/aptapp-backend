package com.creatix.security;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.entity.store.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws AuthenticationException {
        final Account account = this.accountDao.findByEmail(username);

        if ( account == null ) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        else if ( account.getActive() == Boolean.FALSE ) {
            throw new DisabledException(String.format("Account %s is not activated", username));
        }
        else if ( account.getDeletedAt() != null ) {
            throw new AccountExpiredException(String.format("Account %s is deleted", username));
        }
        else {
            return new AuthenticatedUserDetails(account);
        }
    }

}
