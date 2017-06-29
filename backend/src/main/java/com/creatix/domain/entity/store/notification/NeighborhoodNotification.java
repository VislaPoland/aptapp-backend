package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.enums.NeighborhoodNotificationResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(indexes = {
        @Index(columnList = "target_apartment_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class NeighborhoodNotification extends Notification {

    @Column
    @Enumerated(EnumType.STRING)
    private NeighborhoodNotificationResponse response;

    /**
     * If recipient of notification returns recipient account as tenant class.
     * Otherwise returns null
     * @return Recipient account as Tenant or null
     */
    @Nullable
    @Transient
    public Tenant getRecipientAsTenant() {
        // Guard
        if (null == this.getRecipient()) return null;

        switch (this.getRecipient().getRole()) {
            case Tenant:
                return (Tenant) this.getRecipient();
            default:
                return null;
        }
    }
}
