package com.creatix.domain.entity.store.notification;

import com.creatix.domain.enums.SecurityNotificationResponseType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.OffsetDateTime;

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

}
