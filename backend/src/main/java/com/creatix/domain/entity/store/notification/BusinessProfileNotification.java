package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by kvimbi on 27/06/2017.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class BusinessProfileNotification extends Notification {

    @ManyToOne(cascade = CascadeType.ALL)
    private BusinessProfile businessProfile;

    public BusinessProfileNotification() {
        this.type = NotificationType.BusinessProfile;
    }

    @Override
    public void setType(NotificationType type) {}

}
