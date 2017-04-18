package com.creatix.message.push;

import com.creatix.message.PushNotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class GenericPushNotification {

    private Integer badgeCount;

    private String title;

    private String message;

    private PushNotificationType pushNotificationType = PushNotificationType.INFO;

    private Map<String, String> attributes = new HashMap<>();

}
