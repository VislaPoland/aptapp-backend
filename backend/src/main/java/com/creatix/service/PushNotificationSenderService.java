package com.creatix.service;

import com.creatix.configuration.PushNotificationProperties;
import com.creatix.domain.entity.push.notification.PushNotification;
import com.creatix.domain.entity.store.account.device.Device;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.notnoop.apns.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@Transactional
public class PushNotificationSenderService {

    @Autowired
    private PushNotificationProperties pushNotificationProperties;

    private ApnsService apnsService;
    private Sender gcmSender;

    @PostConstruct
    private void init() {
        InputStream certificate = getClass().getClassLoader().getResourceAsStream(pushNotificationProperties.getAppleCertificatePath());
        if (certificate == null) {
            throw new ResourceAccessException("APNS certificate does not exists!");
        }
        ApnsServiceBuilder builder = APNS.newService()
                .withCert(certificate, this.pushNotificationProperties.getAppleCertificatePassword());
        if (this.pushNotificationProperties.isAppleSandbox()) {
            builder.withSandboxDestination();
        }
        else {
            builder.withProductionDestination();
        }
        this.apnsService = builder.build();

        if (this.pushNotificationProperties.getGoogleCloudMessagingKey() == null) {
            throw new IllegalStateException("Google GCM key property is null");
        }
        this.gcmSender = new Sender(this.pushNotificationProperties.getGoogleCloudMessagingKey());
    }

    public void sendNotification(@NotNull PushNotification notification, @NotNull Device toDevice) throws IOException {
        switch (toDevice.getPlatform()) {
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

    private void sendAPNS(@NotNull PushNotification notification, @NotNull Device device) {
        PayloadBuilder payloadBuilder = APNS.newPayload();
        if (notification.getBadgeCount() != null) {
            payloadBuilder.badge(notification.getBadgeCount());
        }
        if (notification.getTitle() != null) {
            payloadBuilder.alertTitle(notification.getTitle());
        }
        if (notification.getMessage() != null) {
            payloadBuilder.alertBody(notification.getMessage());
        }
        if (notification.getBuilder() != null) {
            payloadBuilder.customFields(notification.getBuilder().notificationRepresentation());
        }
        ApnsNotification apnsNotification = this.apnsService.push(device.getPushToken(), payloadBuilder.build());
    }

    private void sendGCM(@NotNull PushNotification notification, @NotNull Device device) throws IOException {
        Message.Builder messageBuilder = new Message.Builder();
        if (notification.getBadgeCount() != null) {
            messageBuilder.addData("badge", String.valueOf(notification.getBadgeCount()));
        }
        if (notification.getTitle() != null) {
            messageBuilder.addData("title", notification.getTitle());
        }
        if (notification.getMessage() != null) {
            messageBuilder.addData("body", notification.getMessage());
        }
        if (notification.getBuilder() != null) {
            for (Map.Entry<String, String> entry : notification.getBuilder().notificationRepresentation().entrySet()) {
                messageBuilder.addData(entry.getKey(), entry.getValue());
            }
        }
        Message message = messageBuilder.build();
        this.gcmSender.send(message, device.getPushToken(), 1);
    }

}
