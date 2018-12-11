package com.creatix.message.template.push;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.PushNotificationTemplateName;

import javax.annotation.Nonnull;
import java.time.Duration;

public class EscalatedManagerNotificationTemplate extends PushMessageTemplate {

    private final String offenderUnit;
    private final String complainerUnit;


    public EscalatedManagerNotificationTemplate(@Nonnull String offenderUnit, String complainerUnit) {
        this.offenderUnit = offenderUnit;
        this.complainerUnit = complainerUnit;
    }

    @Override
    public String getTemplateName() {
        return complainerUnit == null ? PushNotificationTemplateName.ESCALATED_MANAGER_NOTIFICATION_MORE.getValue() : PushNotificationTemplateName.ESCALATED_MANAGER_NOTIFICATION.getValue();
    }

    @Nonnull
    public String getOffenderUnit() {
        return this.offenderUnit;
    }

    public String getComplainerUnit() {
        return this.complainerUnit;
    }
}
