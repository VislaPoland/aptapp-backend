package com.creatix.configuration.versioning;

import com.creatix.configuration.CorsRegistryConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by Tomas Michalek on 24/04/2017.
 */
@Configuration
public class VersionRequestMapperConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private CorsRegistryConfigurationService corsRegistryConfigurationService;

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        corsRegistryConfigurationService.addMappings(registry);
    }

    @Bean
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {

        VersionRequestMapper versionRequestMapper = new VersionRequestMapper();
        versionRequestMapper.setOrder(3);
        versionRequestMapper.setInterceptors(getInterceptors());
        versionRequestMapper.setContentNegotiationManager(mvcContentNegotiationManager());
        versionRequestMapper.setCorsConfigurations(getCorsConfigurations());

        PathMatchConfigurer configurer = getPathMatchConfigurer();
        if (configurer.isUseSuffixPatternMatch() != null) {
            versionRequestMapper.setUseSuffixPatternMatch(configurer.isUseSuffixPatternMatch());
        }
        if (configurer.isUseRegisteredSuffixPatternMatch() != null) {
            versionRequestMapper.setUseRegisteredSuffixPatternMatch(configurer.isUseRegisteredSuffixPatternMatch());
        }
        if (configurer.isUseTrailingSlashMatch() != null) {
            versionRequestMapper.setUseTrailingSlashMatch(configurer.isUseTrailingSlashMatch());
        }
        if (configurer.getPathMatcher() != null) {
            versionRequestMapper.setPathMatcher(configurer.getPathMatcher());
        }
        if (configurer.getUrlPathHelper() != null) {
            versionRequestMapper.setUrlPathHelper(configurer.getUrlPathHelper());
        }
        return versionRequestMapper;
    }

}
