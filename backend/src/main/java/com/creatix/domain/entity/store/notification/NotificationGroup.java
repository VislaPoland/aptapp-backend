package com.creatix.domain.entity.store.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class NotificationGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @OneToMany(mappedBy = "notificationGroup")
    private List<Notification> notifications = new ArrayList<>(0);

    public void addNotification(@Nonnull Notification notification) {
        Objects.requireNonNull(notification, "notification");

        notification.setNotificationGroup(this);
        notifications.add(notification);
    }

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
