package com.creatix.configuration;

import com.creatix.security.AccountDeviceFilter;
import com.creatix.security.AuthenticationTokenFilter;
import com.creatix.security.EntryPointUnauthorizedHandler;
import com.creatix.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private EntryPointUnauthorizedHandler unauthorizedHandler;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public TokenUtils tokenUtils() {
        final TokenUtils tokenUtils = new TokenUtils();
        tokenUtils.setExpiration(jwtProperties.getExpiration());
        tokenUtils.setSecret(jwtProperties.getSecret());
        return tokenUtils;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }

    @Bean
    public AccountDeviceFilter deviceFilterBean() {
        AccountDeviceFilter accountDeviceFilter = new AccountDeviceFilter();
        return accountDeviceFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .exceptionHandling()
                .authenticationEntryPoint(this.unauthorizedHandler)
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/verify-code").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/attempt").permitAll()
                .antMatchers(HttpMethod.POST, "/api/account/request-reset/password").permitAll()
                .antMatchers(HttpMethod.POST, "/api/account/reset/password").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/notifications/*/photos/*").permitAll()
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
                .and()
            .csrf()
                .disable();

        // JWT authentication
        http.addFilter(authenticationTokenFilterBean());
        http.addFilterAfter(deviceFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }


}
