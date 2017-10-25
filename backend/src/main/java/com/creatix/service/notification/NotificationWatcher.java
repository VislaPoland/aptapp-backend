package com.creatix.service.notification;

import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
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
    private final Map<Long, PropertyNotificationWatcher> watcherMap = new HashMap<>();

    public NotificationWatcher(@Nonnull NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public void process(@Nonnull NeighborhoodNotification notification) {
        final Long propertyId = notification.getProperty().getId();
        synchronized ( watcherMap ) {
            PropertyNotificationWatcher watcher = watcherMap.get(propertyId);
            if ( watcher == null ) {
                watcher = new PropertyNotificationWatcher(notificationDao, notification.getProperty());
                watcherMap.put(propertyId, watcher);
            }
            watcher.processNotification(notification);
        }
    }

}
