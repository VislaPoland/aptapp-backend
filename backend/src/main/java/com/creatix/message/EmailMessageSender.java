package com.creatix.message;

import com.creatix.configuration.MailProperties;
import com.creatix.message.template.EmailMessageTemplate;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class EmailMessageSender {

    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private EmailTemplateProcessor templateProcessor;

    public void send(EmailMessageTemplate template) throws IOException, TemplateException, MessageDeliveryException {
        send(template.getSubject(), templateProcessor.processTemplate(template), template.getRecipient());
    }

    public void send(String subject, String body, String recipientEmail) throws MessageDeliveryException {

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
