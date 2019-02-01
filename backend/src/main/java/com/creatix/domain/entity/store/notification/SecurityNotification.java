package com.creatix.domain.entity.store.notification;

import com.creatix.domain.enums.SecurityNotificationResponseType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Entity
@BatchSize(size = 40)
@EqualsAndHashCode(callSuper = true)
public class SecurityNotification extends Notification {

    @Column
    @Enumerated(EnumType.STRING)
    private SecurityNotificationResponseType response;

    @Column
    private OffsetDateTime respondedAt;

    @Column
    private OffsetDateTime closedAt;

    @OneToMany(mappedBy = "notification")
    private List<NotificationHistory> history;

}
