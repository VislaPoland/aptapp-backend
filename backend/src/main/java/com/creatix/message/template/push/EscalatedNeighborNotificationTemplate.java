package com.creatix.message.template.push;

import com.creatix.domain.enums.PushNotificationTemplateName;
import org.omg.CORBA.PUBLIC_MEMBER;

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
        return PushNotificationTemplateName.ESCALATED_NEIGHBOR_NOTIFICATION.getValue();
    }

    public int getTimes() {
        return threshold;
    }

    public int getHours() {
        return (int) (duration.getSeconds() / 3600);
    }
}
