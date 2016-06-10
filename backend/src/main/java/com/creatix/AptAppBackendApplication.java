package com.creatix;

import com.creatix.configuration.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
        SwaggerConfiguration.class
})
@EnableConfigurationProperties({
        MailProperties.class,
        JwtProperties.class
})
@Order(1)
public class AptAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AptAppBackendApplication.class, args);
    }

}
