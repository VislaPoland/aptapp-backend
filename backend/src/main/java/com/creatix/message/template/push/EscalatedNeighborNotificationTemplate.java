package com.creatix.message.template.push;

import java.time.Duration;

public class EscalatedNeighborNotificationTemplate extends PushMessageTemplate {

    private final int threshold;
    private final Duration duration;

    public EscalatedNeighborNotificationTemplate(int threshold, Duration duration) {
        this.threshold = threshold;
        this.duration = duration;
    }

    @Override
    public String getTemplateName() {
        return "escalated-neighbor-notification";
    }

    public int getTimes() {
        return threshold;
    }

    public int getHours() {
        return (int) (duration.getSeconds() / 3600);
    }
}
