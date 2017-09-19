package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.EventInvite;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by kvimbi on 15/06/2017.
 */
@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class EventInviteNotification extends Notification {

    @OneToOne
    private EventInvite eventInvite;

    public EventInviteNotification() {
        this.type = NotificationType.EventInvite;
    }

    @Override
    public void setType(NotificationType type) {}

}
