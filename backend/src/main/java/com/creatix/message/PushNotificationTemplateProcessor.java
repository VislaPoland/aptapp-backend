package com.creatix.message;

import org.springframework.stereotype.Component;

@Component
public class PushNotificationTemplateProcessor extends TemplateProcessor {

    @Override
    protected String subPath() {
        return "push";
    }
}
