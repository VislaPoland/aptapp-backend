package com.creatix.domain.entity.store.notification;

import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by kvimbi on 29/05/2017.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonalMessageNotification extends Notification {

    @ManyToOne
    private PersonalMessage personalMessage;

    public PersonalMessageNotification() {
        this.type = NotificationType.PersonalMessage;
    }

    @Override
    public void setType(NotificationType type) {}

}
