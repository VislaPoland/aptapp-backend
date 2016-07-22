package com.creatix.domain.entity.store.notification;

import com.creatix.domain.enums.SecurityNotificationResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityNotification extends Notification {

    @Column
    @Enumerated(EnumType.STRING)
    private SecurityNotificationResponse response;

}
