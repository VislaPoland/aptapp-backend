package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NeighborhoodNotificationResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class NeighborhoodNotification extends Notification {

    @ManyToOne
    @JoinColumn
    private Apartment targetApartment;

    @Column
    private OffsetDateTime respondedAt;

    @Column
    @Enumerated(EnumType.STRING)
    private NeighborhoodNotificationResponse response;

    /**
     * If recipient of notification returns recipient account as tenant class.
     * Otherwise returns null
     *
     * @return Recipient account as Tenant or null
     */
    @Nullable
    @Transient
    public Tenant getRecipientAsTenant() {
        // Guard
        if (null == this.getRecipient()) {
            return null;
        }

        if (AccountRole.Tenant.equals(this.getRecipient().getRole())) {
            return (Tenant) this.getRecipient();
        }

        return null;
    }
}
