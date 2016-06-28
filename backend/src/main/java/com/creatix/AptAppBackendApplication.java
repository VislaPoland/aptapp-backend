package com.creatix;

import com.creatix.configuration.*;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
@Import({
        WebMvcConfiguration.class,
        WebSecurityConfiguration.class,
        MethodSecurityConfiguration.class,
        JpaConfiguration.class,
        SwaggerConfiguration.class,
})
@EnableConfigurationProperties({
        MailProperties.class,
        JwtProperties.class,
        FileUploadProperties.class,
        TwilioProperties.class,
})
public class AptAppBackendApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AptAppBackendApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AptAppBackendApplication.class);
    }

    @Bean
    public MapperFactory mapperFactory() {
        return new DefaultMapperFactory.Builder().build();
    }

}
