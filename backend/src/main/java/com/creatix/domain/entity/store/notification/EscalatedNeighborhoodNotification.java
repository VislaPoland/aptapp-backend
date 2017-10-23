package com.creatix.domain.entity.store.notification;

import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;

/**
 * Created by kvimbi on 15/06/2017.
 */
@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class EscalatedNeighborhoodNotification extends NeighborhoodNotification {

    public EscalatedNeighborhoodNotification() {
        this.type = NotificationType.Escalation;
    }

    @Override
    public void setType(NotificationType type) {}

}
