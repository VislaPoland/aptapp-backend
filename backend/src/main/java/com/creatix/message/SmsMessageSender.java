package com.creatix.message;

import com.creatix.configuration.TwilioProperties;
import com.creatix.message.template.sms.SmsMessageTemplate;
import com.twilio.sdk.Twilio;
import com.twilio.sdk.creator.api.v2010.account.MessageCreator;
import com.twilio.sdk.resource.api.v2010.account.Message;
import com.twilio.sdk.type.PhoneNumber;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SmsMessageSender {

    @Autowired
    private TwilioProperties twilioProperties;
    @Autowired
    private SmsTemplateProcessor templateProcessor;

    private boolean isInitialized = false;

    public void send(SmsMessageTemplate template) throws IOException, TemplateException, MessageDeliveryException {
        send(templateProcessor.processTemplate(template), template.getRecipient());
    }

    /**
     * Sent SMS message to phone number.
     *
     * @param body SMS message text. Example: "Hello from Java"
     * @param recipientPhone SMS recipient phone number. Example: "+12345678901"
     */
    private void send(String body, String recipientPhone) throws MessageDeliveryException {

        if ( StringUtils.isBlank(twilioProperties.getAccountSid()) ) {
            throw new IllegalStateException("Missing account sid configuration");
        }
        if ( StringUtils.isBlank(twilioProperties.getAuthToken()) ) {
            throw new IllegalStateException("Missing auth token configuration");
        }
        if ( StringUtils.isBlank(twilioProperties.getFrom()) ) {
            throw new IllegalArgumentException("Missing from number configuration");
        }

        if ( !(isInitialized) ) {
            Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
            isInitialized = true;
        }

        final Message message = new MessageCreator(
                twilioProperties.getAccountSid(),
                new PhoneNumber(recipientPhone),
                new PhoneNumber(twilioProperties.getFrom()),
                body
        ).execute();

        if ( message.getStatus() == Message.Status.FAILED ) {
            throw new MessageDeliveryException(String.format("SMS delivery failed. Error %d: %s", message.getErrorCode(), message.getErrorMessage()));
        }
    }

}
