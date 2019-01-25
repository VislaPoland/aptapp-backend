package com.creatix.message;

import com.creatix.configuration.TwilioProperties;
import com.creatix.message.template.sms.SmsMessageTemplate;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import freemarker.template.TemplateException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.creatix.message.SmsMessageSender.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class SmsMessageSenderTest {

    private static final String TEST_EXCEPTION = "test exception";

    @MockBean
    private TwilioProperties twilioProperties;

    @MockBean
    private SmsTemplateProcessor templateProcessor;

    @Mock
    private SmsMessageTemplate messageTemplate;

    @Mock
    private Message message;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @SpyBean
    private SmsMessageSender smsMessageSender;

    @Test
    public void shouldNotDoAnythingWhenNull() throws MessageDeliveryException, TemplateException, IOException {

        smsMessageSender.send(null);

        verifyNoMoreInteractions(templateProcessor);
    }

    @Test
    public void shouldNotDoAnythingWhenNoRecipient() throws MessageDeliveryException, TemplateException, IOException {
        when(messageTemplate.getRecipient()).thenReturn(null);
        smsMessageSender.send(messageTemplate);

        verifyNoMoreInteractions(templateProcessor);
    }

    @Test
    public void shouldCatchExceptionWhenTwillioNumberBlacklisted() throws MessageDeliveryException, TemplateException, IOException {
        when(twilioProperties.getAccountSid()).thenReturn("ACCOUNT_SID");
        when(twilioProperties.getAuthToken()).thenReturn("AUTH_TOKEN");
        when(twilioProperties.getFrom()).thenReturn("FROM");

        when(messageTemplate.getRecipient()).thenReturn("0900123456");
        when(templateProcessor.processTemplate(any())).thenReturn("test message");
        doThrow(new ApiException(THE_MESSAGE_FROM_TO_PAIR_VIOLATES_A_BLACKLIST_RULE)).when(smsMessageSender).invokeMessageSend(any(),any());

        // be sure we will not create instance of Twillio
        smsMessageSender.setInitialized(true);
        smsMessageSender.send(messageTemplate);
    }

    @Test
    public void shouldFailWhenSidMissing() throws MessageDeliveryException, TemplateException, IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(MISSING_ACCOUNT_SID_CONFIGURATION);

        when(messageTemplate.getRecipient()).thenReturn("0900123456");

        smsMessageSender.send(messageTemplate);
    }

    @Test
    public void shouldFailWhenTokenMissing() throws MessageDeliveryException, TemplateException, IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(MISSING_AUTH_TOKEN_CONFIGURATION);

        when(twilioProperties.getAccountSid()).thenReturn("ACCOUNT_SID");
        when(messageTemplate.getRecipient()).thenReturn("0900123456");

        smsMessageSender.send(messageTemplate);
    }

    @Test
    public void shouldFailWhenFromMissing() throws MessageDeliveryException, TemplateException, IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MISSING_FROM_NUMBER_CONFIGURATION);

        when(twilioProperties.getAccountSid()).thenReturn("ACCOUNT_SID");
        when(twilioProperties.getAuthToken()).thenReturn("AUTH_TOKEN");
        when(messageTemplate.getRecipient()).thenReturn("0900123456");

        smsMessageSender.send(messageTemplate);
    }

    @Test
    public void shouldFailWhenUnableToSendMessage() throws MessageDeliveryException, TemplateException, IOException {
        thrown.expect(MessageDeliveryException.class);
        thrown.expectMessage("SMS delivery failed. Error 12: ".concat(TEST_EXCEPTION));

        when(twilioProperties.getAccountSid()).thenReturn("ACCOUNT_SID");
        when(twilioProperties.getAuthToken()).thenReturn("AUTH_TOKEN");
        when(twilioProperties.getFrom()).thenReturn("FROM");

        when(messageTemplate.getRecipient()).thenReturn("0900123456");

        doThrow(new ApiException(TEST_EXCEPTION, 12, null, null, null)).when(smsMessageSender).invokeMessageSend(any(),any());

        // be sure we will not create instance of Twillio
        smsMessageSender.setInitialized(true);
        smsMessageSender.send(messageTemplate);
    }

    @Test
    public void shouldFailWhenMessageReturnsError() throws MessageDeliveryException, TemplateException, IOException {
        thrown.expect(MessageDeliveryException.class);
        thrown.expectMessage("SMS delivery failed. Error 12: ".concat(TEST_EXCEPTION));

        when(twilioProperties.getAccountSid()).thenReturn("ACCOUNT_SID");
        when(twilioProperties.getAuthToken()).thenReturn("AUTH_TOKEN");
        when(twilioProperties.getFrom()).thenReturn("FROM");

        when(messageTemplate.getRecipient()).thenReturn("0900123456");

        when(message.getStatus()).thenReturn(Message.Status.FAILED);
        when(message.getErrorCode()).thenReturn(12);
        when(message.getErrorMessage()).thenReturn(TEST_EXCEPTION);

        doReturn(message).when(smsMessageSender).invokeMessageSend(any(),any());

        // be sure we will not create instance of Twillio
        smsMessageSender.setInitialized(true);
        smsMessageSender.send(messageTemplate);
    }
}