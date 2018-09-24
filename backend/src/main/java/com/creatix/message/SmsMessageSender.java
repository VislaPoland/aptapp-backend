package com.creatix.message;

import com.creatix.configuration.TwilioProperties;
import com.creatix.message.template.sms.SmsMessageTemplate;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class SmsMessageSender {

    @Autowired
    private TwilioProperties twilioProperties;
    @Autowired
    private SmsTemplateProcessor templateProcessor;

    private boolean isInitialized = false;

    public boolean validPhoneNumber(@Nonnull String phoneNumber) {
        return Pattern.compile("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$").matcher(phoneNumber).matches();
    }

    public void send(SmsMessageTemplate template) throws IOException, TemplateException, MessageDeliveryException {
        if ( StringUtils.isNotBlank(template.getRecipient()) ) {
            send(templateProcessor.processTemplate(template), template.getRecipient());
        }
    }

    /**
     * Sent SMS message to phone number.
     *
     * @param body           SMS message text. Example: "Hello from Java"
     * @param recipientPhone SMS recipient phone number. Example: "+12345678901"
     */
    private void send(@Nonnull String body, @Nonnull String recipientPhone) throws MessageDeliveryException {

        if ( StringUtils.isBlank(twilioProperties.getAccountSid()) ) {
            throw new IllegalStateException("Missing account sid configuration");
        }
        if ( StringUtils.isBlank(twilioProperties.getAuthToken()) ) {
            throw new IllegalStateException("Missing auth token configuration");
        }
        if ( StringUtils.isBlank(twilioProperties.getFrom()) ) {
            throw new IllegalArgumentException("Missing from number configuration");
        }

        initializeTwilioIfNeeded();

        final Message message = Message
                .creator(new com.twilio.type.PhoneNumber(recipientPhone),
                         new com.twilio.type.PhoneNumber(twilioProperties.getFrom()),
                         body)
                .create();

        if ( message.getStatus() == Message.Status.FAILED ) {
            throw new MessageDeliveryException(String.format("SMS delivery failed. Error %d: %s", message.getErrorCode(), message.getErrorMessage()));
        }
    }

    private synchronized void initializeTwilioIfNeeded() {
        if ( !(isInitialized) ) {
            Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
            isInitialized = true;
        }
    }

}
