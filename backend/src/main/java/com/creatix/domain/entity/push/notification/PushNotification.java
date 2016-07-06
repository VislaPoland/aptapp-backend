package com.creatix.domain.entity.push.notification;

import lombok.Data;

@Data
public class PushNotification {

    private Integer badgeCount;

    private String title;

    private String message;

    private PushNotificationBuilder builder;

}
