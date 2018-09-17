package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.util.StringUtils;

public class NeighborNotificationTemplate extends PushMessageTemplate {

    protected final NeighborhoodNotification notification;

    public NeighborNotificationTemplate(NeighborhoodNotification notification) {
        this.notification = notification;
    }

    @Override
    public String getTemplateName() {
        return "neighbor-notification";
    }

    public String getMessage() {
        return StringUtils.translateTileFromEnumString(notification.getTitle());
    }

    public String getTimestamp() {
        return formatTimestamp(
                notification.getCreatedAt(),
                notification.getProperty().getZoneId()
        );
    }

}
