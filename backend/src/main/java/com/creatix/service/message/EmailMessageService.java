package com.creatix.service.message;

import com.creatix.configuration.MailProperties;
import com.creatix.message.EmailTemplateProcessor;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.template.email.EmailMessageTemplate;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Component
public class EmailMessageService {

    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private EmailTemplateProcessor templateProcessor;

    public void send(EmailMessageTemplate template) throws IOException, TemplateException, MessageDeliveryException, MessagingException {
        send(template.getSubject(), templateProcessor.processTemplate(template), template.getRecipient());
    }

    private void send(String subject, String body, String recipientEmail) throws MessageDeliveryException, MessagingException {

        if ( StringUtils.isEmpty(mailProperties.getFrom()) ) {
            throw new MessageDeliveryException("[From] address not defined in configuration");
        }

        final MimeMessage mailMessage = mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);
        messageHelper.setFrom(mailProperties.getFrom());
        messageHelper.setTo(recipientEmail);
        messageHelper.setText(body, true);
        messageHelper.setSubject(subject);
        mailSender.send(mailMessage);
    }

}
