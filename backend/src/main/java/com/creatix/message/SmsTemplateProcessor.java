package com.creatix.message;

import org.springframework.stereotype.Component;

@Component
public class SmsTemplateProcessor extends TemplateProcessor {

    @Override
    protected String subPath() {
        return "sms";
    }
}
