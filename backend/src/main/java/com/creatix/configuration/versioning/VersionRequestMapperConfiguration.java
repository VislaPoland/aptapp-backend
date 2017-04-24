package com.creatix.configuration.versioning;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by kvimbi on 24/04/2017.
 */
@Configuration
public class VersionRequestMapperConfiguration extends WebMvcConfigurationSupport {

    @Bean
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        VersionRequestMapper versionRequestMapper = new VersionRequestMapper();
        versionRequestMapper.setOrder(3);
        return versionRequestMapper;
    }

}
