package com.creatix.message;

import com.creatix.configuration.TwilioProperties;
import com.creatix.message.template.sms.SmsMessageTemplate;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import freemarker.template.TemplateException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsMessageSender {

    static final String THE_MESSAGE_FROM_TO_PAIR_VIOLATES_A_BLACKLIST_RULE = "The message From/To pair violates a blacklist rule.";
    static final String MISSING_ACCOUNT_SID_CONFIGURATION = "Missing account sid configuration";
    static final String MISSING_AUTH_TOKEN_CONFIGURATION = "Missing auth token configuration";
    static final String MISSING_FROM_NUMBER_CONFIGURATION = "Missing from number configuration";
    static final String MESSAGE_TEMPLATE_CANNOT_BE_NULL = "Message template cannot be null";

    private static final String SMS_DELIVERY_FAILED_FORMAT = "SMS delivery failed. Error %d: %s";
    private final TwilioProperties twilioProperties;
    private final SmsTemplateProcessor templateProcessor;

    /**
     * setter package-private. Use only for stubbing Twilio instance creation
     */
    @Setter(AccessLevel.PACKAGE)
    private boolean isInitialized = false;

    public boolean validPhoneNumber(@Nonnull String phoneNumber) {
        return Pattern.compile("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$").matcher(phoneNumber).matches();
    }

    public void send(SmsMessageTemplate template) throws IOException, TemplateException, MessageDeliveryException {
        if (template == null) {
            throw new MessageDeliveryException(MESSAGE_TEMPLATE_CANNOT_BE_NULL);
        }

        if (StringUtils.isNotBlank(template.getRecipient())) {
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

        if (StringUtils.isBlank(twilioProperties.getAccountSid())) {
            throw new IllegalStateException(MISSING_ACCOUNT_SID_CONFIGURATION);
        }
        if (StringUtils.isBlank(twilioProperties.getAuthToken())) {
            throw new IllegalStateException(MISSING_AUTH_TOKEN_CONFIGURATION);
        }
        if (StringUtils.isBlank(twilioProperties.getFrom())) {
            throw new IllegalArgumentException(MISSING_FROM_NUMBER_CONFIGURATION);
        }

        initializeTwilioIfNeeded();

        try {
            final Message message = invokeMessageSend(body, recipientPhone);

            if (message.getStatus() == Message.Status.FAILED) {
                throw new MessageDeliveryException(String.format(SMS_DELIVERY_FAILED_FORMAT, message.getErrorCode(), message.getErrorMessage()));
            }
        } catch (ApiException apiException) {
            if (isBlacklisted(apiException)) {
                log.warn("receiver {} has blacklisted our twilio number", recipientPhone);

                return;
            }

            throw new MessageDeliveryException(String.format(SMS_DELIVERY_FAILED_FORMAT, apiException.getCode(), apiException.getMessage()));
        }
    }

    /**
     * return if it is specific exception connected with blacklisted number
     *
     * @param twilioException exception given from twilio
     * @return if exception is our known business error which should be handled properly.
     * see https://support.twilio.com/hc/en-us/articles/223133627--The-message-From-To-pair-violates-a-blacklist-rule-when-sending-messages
     */
    private boolean isBlacklisted(TwilioException twilioException) {
        return twilioException.getMessage() != null
                && twilioException.getMessage().contains(THE_MESSAGE_FROM_TO_PAIR_VIOLATES_A_BLACKLIST_RULE);
    }

    /**
     * separated invoke message to be able to mock/stub this metod
     *
     * @param body           what will be send
     * @param recipientPhone who will receive message
     * @return invocation result
     */
    Message invokeMessageSend(@Nonnull String body, @Nonnull String recipientPhone) {
        return Message
                .creator(new com.twilio.type.PhoneNumber(recipientPhone),
                        new com.twilio.type.PhoneNumber(twilioProperties.getFrom()),
                        body)
                .create();
    }

    private synchronized void initializeTwilioIfNeeded() {
        if (!(isInitialized)) {
            Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
            isInitialized = true;
        }
    }

}
