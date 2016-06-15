package com.creatix;

import com.creatix.configuration.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
@Import({
        WebMvcConfiguration.class,
        WebSecurityConfiguration.class,
        MethodSecurityConfiguration.class,
        SwaggerConfiguration.class
})
@EnableConfigurationProperties({
        MailProperties.class,
        JwtProperties.class
})
@Order(1)
public class AptAppBackendApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AptAppBackendApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AptAppBackendApplication.class);
    }

}
