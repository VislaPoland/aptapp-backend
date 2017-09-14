package com.creatix.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter requests using JWT (json web token). Token is stored in HTTP header.
 */
public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${jwt.token.header}")
    private String tokenHeader;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private AuthenticatedUserDetailsService userDetailsService;

    private final AntPathRequestMatcher MATCH_LOGIN = new AntPathRequestMatcher("/api/auth/attempt", HttpMethod.POST.name());

    public JwtAuthenticationTokenFilter() {
        super(new AntPathRequestMatcher("/api/**"));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);

            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Request is to process authentication");
        }

        Authentication authResult;

        try {
            authResult = attemptAuthentication(request, response);
            if (authResult == null) {
                // return immediately as subclass has indicated that it hasn't completed
                // authentication
                return;
            }
        }
        catch (InternalAuthenticationServiceException failed) {
            logger.error(
                    "An internal error occurred while trying to authenticate the user.",
                    failed);
            unsuccessfulAuthentication(request, response, failed);

            return;
        }
        catch (AuthenticationException failed) {
            // Authentication failed
            unsuccessfulAuthentication(request, response, failed);

            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String authToken = extractAuthToken(request.getHeader(this.tokenHeader));
        if ( authToken == null ) {
            // enable swagger api key authentication
            authToken = request.getHeader("api_key");
        }

        final String username = this.tokenUtils.getUsernameFromToken(authToken);

        final Authentication authentication;
        if ( isLoginEndpoint(request, response) ) {
            // If user is logging in we need to ignore existing token, otherwise user might not be able to log into the system.
            authentication = getAnonymousAuthentication();
        }
        else if ( isNotAuthenticated(SecurityContextHolder.getContext().getAuthentication()) ) {
            if ( username != null ) {
                final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if ( this.tokenUtils.validateToken(authToken, userDetails) ) {
                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    ((UsernamePasswordAuthenticationToken) authentication).setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                }
                else {
                    throw new IllegalArgumentException("JWT token is invalid");
                }
            }
            else {
                authentication = getAnonymousAuthentication();
            }
        }
        else {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }

        return authentication;
    }

    private AnonymousAuthenticationToken getAnonymousAuthentication() {
        return new AnonymousAuthenticationToken("anonymousToken", "anonymous", anonymousAuthority());
    }

    protected boolean isLoginEndpoint(HttpServletRequest request,
                                      HttpServletResponse response) {
        return MATCH_LOGIN.matches(request);
    }

    private boolean isNotAuthenticated(Authentication authentication) {
        if ( authentication == null ) {
            return true;
        }
        if ( !(authentication.isAuthenticated()) ) {
            return true;
        }
        if ( authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS")) ) {
            return true;
        }

        return false;
    }

    private static String extractAuthToken(String authHeader) {
        if ( authHeader != null ) {
            Pattern p = Pattern.compile("[Bb]earer (.*)");
            Matcher m = p.matcher(authHeader);
            if ( m.find() ) {
                return m.group(1);
            }
        }

        return null;
    }

    private static List<GrantedAuthority> anonymousAuthority() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        return authorities;
    }
}
