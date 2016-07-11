package com.creatix.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableWebMvc
@Configuration
public class WebMvcConfiguration extends WebMvcAutoConfiguration {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private DeviceProperties deviceProperties;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {

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
        };
    }

}
