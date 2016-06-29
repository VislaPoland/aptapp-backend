package com.creatix.security;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.entity.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Account account = this.accountDao.findByEmail(username);

        if ((account == null)) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return new AuthenticatedUserDetails(account);
        }
    }

}
