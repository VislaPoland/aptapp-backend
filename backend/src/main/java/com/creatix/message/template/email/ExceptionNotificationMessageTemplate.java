package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tomas Sedlak on 9.10.2017.
 */
public class ExceptionNotificationMessageTemplate implements EmailMessageTemplate {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String recipient;
    private final Throwable t;
    private final ApplicationProperties properties;

    public ExceptionNotificationMessageTemplate(Throwable t, String recipient, ApplicationProperties properties) {
        this.t = t;
        this.recipient = recipient;
        this.properties = properties;
    }

    @Override
    public String getTemplateName() {
        return "exception-notification";
    }

    public String getExceptionMessage() {
        return StringEscapeUtils.escapeHtml4(ExceptionUtils.getMessage(t));
    }

    public String getExceptionStackTrace() {
        return StringEscapeUtils.escapeHtml4(ExceptionUtils.getStackTrace(t));
    }

    public String getExceptionTime() {
        return DATE_FORMAT.format(new Date());
    }

    public String getApplicationUrl() {
        return properties.getAdminUrl().toString();
    }

    @Override
    public String getSubject() {
        return "AptApp exception";
    }

    @Override
    public String getRecipient() {
        return recipient;
    }
}
