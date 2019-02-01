package com.creatix.message.template.sms;

import com.creatix.domain.enums.SmsTemplateName;

import javax.annotation.Nonnull;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
public class EscalatedManagerSmsTemplate implements SmsMessageTemplate {

    @Nonnull
    private final String managerNumber;
    @Nonnull
    private final String offenderUnit;
    @Nonnull
    private final String complainerUnit;

    public EscalatedManagerSmsTemplate(String managerNumber, @Nonnull String offenderUnit, @Nonnull String complainerUnit) {
        this.managerNumber = managerNumber;
        this.offenderUnit = offenderUnit;
        this.complainerUnit = complainerUnit;
    }

    @Override
    public String getRecipient() {
        return managerNumber;
    }

    @Override
    public String getTemplateName() {
        return SmsTemplateName.ESCALATED_NOTIFICATION_ONE.getValue();
    }

    public String getOffenderUnit() {
        return offenderUnit;
    }

    public String getComplainerUnit() {
        return complainerUnit;
    }
}
