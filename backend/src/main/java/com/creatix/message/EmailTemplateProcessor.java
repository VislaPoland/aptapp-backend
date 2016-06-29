package com.creatix.message;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateProcessor extends TemplateProcessor {

    @Override
    protected String subPath() {
        return "email";
    }
}
