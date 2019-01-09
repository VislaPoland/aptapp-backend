package com.creatix.configuration.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;

/**
 * Configuration used for running application in local environment
 * without need of configured mail server on local machine
 */
@Configuration
@Profile("local")
@Slf4j
public class LocalProfileBeansConfiguration {

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl() {
            @Override
            public void send(MimeMessage mimeMessage) throws MailException {
                log.info("You are in local profile, email message is not send, message = {}", mimeMessage.toString());
            }
        };
    }
}
