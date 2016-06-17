package com.creatix.configuration;

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

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                        .allowedOrigins("http://127.0.0.1:3000", "http://localhost:3000", "http://aptapp-demo.herokuapp.com")
                        .allowedHeaders("Accept-Encoding", "Accept-Language", "User-Agent", "Connection", "Timezone-Offset", "Origin", "X-Requested-With", "Content-Type", "Accept", jwtProperties.getHeader())
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

}
