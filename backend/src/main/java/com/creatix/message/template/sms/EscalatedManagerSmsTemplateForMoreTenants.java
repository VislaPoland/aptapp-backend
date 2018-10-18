package com.creatix.message.template.sms;

import javax.annotation.Nonnull;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
public class EscalatedManagerSmsTemplateForMoreTenants implements SmsMessageTemplate {
    @Nonnull
    private final String managerNumber;
    @Nonnull
    private final String offenderUnit;

    public EscalatedManagerSmsTemplateForMoreTenants(String managerNumber, @Nonnull String offenderUnit) {
        this.managerNumber = managerNumber;
        this.offenderUnit = offenderUnit;
    }

    @Override
    public String getRecipient() {
        return managerNumber;
    }

    @Override
    public String getTemplateName() {
        return "escalated-notification-for-more-tenants";
    }

    public String getOffenderUnit() {
        return offenderUnit;
    }
}
