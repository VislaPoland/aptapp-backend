package com.creatix.message.template.push;

import com.creatix.domain.entity.store.account.Account;

import javax.annotation.Nonnull;
import java.time.Duration;

public class EscalatedManagerNotificationTemplate extends PushMessageTemplate {

    private final int threshold;
    @Nonnull
    private final Account offender;
    @Nonnull
    private final Account complainer;
    @Nonnull
    private final Duration duration;

    public EscalatedManagerNotificationTemplate(@Nonnull Account offender, @Nonnull Account complainer, int threshold, @Nonnull Duration duration) {
        this.threshold = threshold;
        this.duration = duration;
        this.offender = offender;
        this.complainer = complainer;
    }

    @Override
    public String getTemplateName() {
        return "escalated-manager-notification";
    }

    public int getTimes() {
        return threshold;
    }

    public int getHours() {
        return (int) (duration.getSeconds() / 3600);
    }

    @Nonnull
    public String getOffender() {
        return this.offender.getFullName();
    }

    @Nonnull
    public String getComplainer() {
        return this.complainer.getFullName();
    }
}
