package com.creatix.configuration;

import com.creatix.security.RoleSecuredAnnotationMetadataExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(5)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {


    @Bean
    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource()
    {
        return new SecuredAnnotationSecurityMetadataSource(new RoleSecuredAnnotationMetadataExtractor());
    }

}
