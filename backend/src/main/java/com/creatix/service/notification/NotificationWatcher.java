package com.creatix.service.notification;

import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.service.message.PushNotificationSender;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tomas Sedlak on 23.10.2017.
 */
@Component
public class NotificationWatcher {

    @Nonnull
    private final NotificationDao notificationDao;
    @Nonnull
    private final PushNotificationSender pushNotificationSender;
    @Nonnull
    private final Map<Long, PropertyNotificationWatcher> watcherMap = new HashMap<>();

    public NotificationWatcher(@Nonnull NotificationDao notificationDao, @Nonnull PushNotificationSender pushNotificationSender) {
        this.notificationDao = notificationDao;
        this.pushNotificationSender = pushNotificationSender;
    }

    public void process(@Nonnull NeighborhoodNotification notification) throws IOException, TemplateException {
        final Long propertyId = notification.getProperty().getId();
        synchronized ( watcherMap ) {
            PropertyNotificationWatcher watcher = watcherMap.get(propertyId);
            if ( watcher == null ) {
                watcher = new PropertyNotificationWatcher(notification.getProperty(), notificationDao, pushNotificationSender);
                watcherMap.put(propertyId, watcher);
            }
            watcher.processNotification(notification);
        }
    }

}
