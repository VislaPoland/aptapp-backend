package com.creatix.service.message;

import com.creatix.configuration.PushNotificationProperties;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.message.push.GenericPushNotification;
import com.creatix.message.PushNotificationTemplateProcessor;
import com.creatix.message.template.push.PushMessageTemplate;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.notnoop.apns.*;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class PushNotificationService {

    @Autowired
    private PushNotificationProperties pushNotificationProperties;
    @Autowired
    private PushNotificationTemplateProcessor templateProcessor;

    private ApnsService apnsService;
    private Sender gcmSender;

    @PostConstruct
    private void init() {
        InputStream certificate = getClass().getClassLoader().getResourceAsStream(pushNotificationProperties.getAppleCertificatePath());
        if ( certificate == null ) {
            throw new ResourceAccessException("APNS certificate does not exists!");
        }
        ApnsServiceBuilder builder = APNS.newService()
                .withCert(certificate, this.pushNotificationProperties.getAppleCertificatePassword());
        if ( this.pushNotificationProperties.isAppleSandbox() ) {
            builder.withSandboxDestination();
        }
        else {
            builder.withProductionDestination();
        }
        this.apnsService = builder.build();

        if ( this.pushNotificationProperties.getGoogleCloudMessagingKey() == null ) {
            throw new IllegalStateException("Google GCM key property is null");
        }
        this.gcmSender = new Sender(this.pushNotificationProperties.getGoogleCloudMessagingKey());
    }

    public void sendNotification(@NotNull PushMessageTemplate template, @NotNull Account recipient) throws IOException, TemplateException {
        final GenericPushNotification notification = new GenericPushNotification();
        notification.setMessage(templateProcessor.processTemplate(template));
        sendNotification(notification, recipient);
    }

    public void sendNotification(@NotNull GenericPushNotification notification, @NotNull Account recipient) throws IOException {
        Objects.requireNonNull(notification, "Notification is null");
        Objects.requireNonNull(recipient, "Recipient is null");

        if ( recipient.getDevices() != null ) {
            for ( Device toDevice : recipient.getDevices() ) {
                if ( !(toDevice.isDeleted()) && StringUtils.isNotBlank(toDevice.getPushToken()) ) {
                    switch ( toDevice.getPlatform() ) {
                        case iOS: {
                            this.sendAPNS(notification, toDevice);
                            break;
                        }
                        case Android: {
                            this.sendGCM(notification, toDevice);
                            break;
                        }
                    }
                }
            }
        }
    }

    private ApnsNotification sendAPNS(@NotNull GenericPushNotification notification, @NotNull Device device) {
        Objects.requireNonNull(notification, "Notification is null");
        Objects.requireNonNull(device, "Device is null");

        if ( StringUtils.isBlank(device.getPushToken()) ) {
            throw new IllegalArgumentException(String.format("Missing push token for device id=%d", device.getId()));
        }

        PayloadBuilder payloadBuilder = APNS.newPayload();
        if ( notification.getBadgeCount() != null ) {
            payloadBuilder.badge(notification.getBadgeCount());
        }
        if ( notification.getTitle() != null ) {
            payloadBuilder.alertTitle(notification.getTitle());
        }
        if ( notification.getMessage() != null ) {
            payloadBuilder.alertBody(notification.getMessage());
        }
        if ( notification.getAttributes() != null ) {
            payloadBuilder.customFields(attributesAsMap(notification.getAttributes()));
        }

        return this.apnsService.push(device.getPushToken(), payloadBuilder.build());
    }

    private void sendGCM(@NotNull GenericPushNotification notification, @NotNull Device device) throws IOException {
        Objects.requireNonNull(notification, "Notification is null");
        Objects.requireNonNull(device, "Device is null");

        if ( StringUtils.isBlank(device.getPushToken()) ) {
            throw new IllegalArgumentException(String.format("Missing push token for device id=%d", device.getId()));
        }

        Message.Builder messageBuilder = new Message.Builder();
        if ( notification.getBadgeCount() != null ) {
            messageBuilder.addData("badge", String.valueOf(notification.getBadgeCount()));
        }
        if ( notification.getTitle() != null ) {
            messageBuilder.addData("title", notification.getTitle());
        }
        if ( notification.getMessage() != null ) {
            messageBuilder.addData("message", notification.getMessage());
        }
        if ( notification.getAttributes() != null ) {
            notification.getAttributes().entrySet().forEach(a -> messageBuilder.addData(a.getKey(), a.getValue()));
        }

        final Message message = messageBuilder.build();
        this.gcmSender.send(message, device.getPushToken(), 1);
    }

    private Map<String, String> attributesAsMap(Map<String, String> attributes) {
        if ( attributes == null ) {
            return Collections.emptyMap();
        }
        else {
            return attributes.entrySet().stream().collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
        }
    }
}
