package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.business.DiscountCoupon;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Tomas Michalek on 27/06/2017.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class DiscountCouponNotification extends Notification {

    @ManyToOne
    DiscountCoupon discountCoupon;

    public DiscountCouponNotification() {
        this.type = NotificationType.DiscountCoupon;
    }

    @Override
    public void setType(NotificationType type) {}
}
