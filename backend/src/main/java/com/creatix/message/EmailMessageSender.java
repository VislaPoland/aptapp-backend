package com.creatix.message;

import com.creatix.configuration.MailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmailMessageSender {

    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private MailSender mailSender;

    private void send(String subject, String body, String recipientEmail) throws MessageDeliveryException {

        if ( StringUtils.isEmpty(mailProperties.getFrom()) ) {
            throw new MessageDeliveryException("'From' address not defined in configuration");
        }

        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailProperties.getFrom());
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailSender.send(mailMessage);
    }

}
