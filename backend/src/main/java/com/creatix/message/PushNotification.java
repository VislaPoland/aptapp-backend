package com.creatix.message;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PushNotification {

    private Integer badgeCount;

    private String title;

    private String message;

    private List<Attribute> attributes;


    @Setter
    @Getter
    public static class Attribute {
        private String name;
        private String value;
    }
}
