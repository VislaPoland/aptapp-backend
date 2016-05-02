package com.creatix.security;

import com.creatix.domain.entity.Account;
import com.creatix.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Account account = this.accountRepository.findByEmail(username);

        if ( (account == null) || (account.getDeleteDate() != null) ) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        else {
            return new AuthenticatedUserDetails(account);
        }
    }

}
