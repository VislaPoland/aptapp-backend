package com.creatix.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Created by Tomas Michalek on 26/04/2017.
 */
@Component
public class CorsRegistryConfigurationService {


    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private DeviceProperties deviceProperties;

    /**
     * Side effects
     */
    public void addMappings(CorsRegistry registry) {

        if ( StringUtils.isBlank(jwtProperties.getHeader()) ) {
            throw new IllegalStateException("JWT header is not configured.");
        }
        if ( StringUtils.isBlank(deviceProperties.getPlatformHeader()) ) {
            throw new IllegalStateException("Device platform header is not configured.");
        }
        if ( StringUtils.isBlank(deviceProperties.getUdidHeader()) ) {
            throw new IllegalStateException("UDID header is not configured.");
        }

        registry.addMapping("/**")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .allowedOrigins("*")
                .allowedHeaders(
                        "Accept-Encoding",
                        "Accept-Language",
                        "User-Agent",
                        "Connection",
                        "Timezone-Offset",
                        "Origin",
                        "X-Requested-With",
                        "Content-Type",
                        "Accept",
                        jwtProperties.getHeader(),
                        deviceProperties.getPlatformHeader(),
                        deviceProperties.getUdidHeader())
                .allowCredentials(true)
                .maxAge(3600);

    }

}
