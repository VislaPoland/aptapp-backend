package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.NotificationHistoryStatus;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class NotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    private Account author;

    @ManyToOne(optional = false)
    private Notification notification;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationHistoryStatus status;

    @ManyToOne(optional = false)
    private Property property;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
    }

    @Override
    public String toString() {
        return "NotificationHistory(status=" + status + ", createdAt=" + createdAt + ")";
    }
}
