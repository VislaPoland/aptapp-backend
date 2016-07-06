package com.creatix.service;

import com.creatix.configuration.DeviceProperties;
import com.creatix.configuration.PushNotificationProperties;
import com.creatix.domain.entity.push.notification.PushNotification;
import com.creatix.domain.entity.store.account.device.Device;
import com.notnoop.apns.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Service
@Transactional
public class PushNotificationSenderService {

    @Autowired
    private PushNotificationProperties pushNotificationProperties;

    private ApnsService apnsService;

    @PostConstruct
    private void init() {
        ApnsServiceBuilder builder = APNS.newService()
                .withCert(getClass().getClassLoader().getResourceAsStream(pushNotificationProperties.getAppleCertificatePath()), pushNotificationProperties.getAppleCertificatePassword());
        if (pushNotificationProperties.isAppleSandbox() == true) {
            builder.withSandboxDestination();
        }
        else {
            builder.withProductionDestination();
        }
        apnsService = builder.build();
    }

    public void sendNotification(@NotNull PushNotification notification, @NotNull Device toDevice) {
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

    private void sendGCM(@NotNull PushNotification notification, @NotNull Device device) {

    }

}
