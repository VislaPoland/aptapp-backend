package com.creatix.mock;

import com.creatix.security.AuthenticatedUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Autowired
    private AuthenticatedUserDetailsService userDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserDetails principal =
                this.userDetailsService.loadUserByUsername(mockUser.value());
        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}