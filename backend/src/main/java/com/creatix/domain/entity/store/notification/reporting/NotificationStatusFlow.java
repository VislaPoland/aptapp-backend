package com.creatix.domain.entity.store.notification.reporting;

import com.creatix.domain.enums.NotificationHistoryStatus;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;

import javax.persistence.*;

/**
 * Entity representation for unify notification statuses into one status
 *
 * e.g. one notification type can have only one termination status and the other can possible have multiple statuses.
 */
@Entity
@Table
@Data
public class NotificationStatusFlow {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationHistoryStatus status;

    @Enumerated(EnumType.STRING)
    private NotificationHistoryStatus globalStatus;

}
