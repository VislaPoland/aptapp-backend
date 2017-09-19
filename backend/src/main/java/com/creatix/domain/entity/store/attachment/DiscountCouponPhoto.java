package com.creatix.domain.entity.store.attachment;

import com.creatix.domain.entity.store.business.DiscountCoupon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class DiscountCouponPhoto extends Attachment {

    @OneToOne
    private DiscountCoupon discountCoupon;

    public DiscountCouponPhoto() {
        this.attachedEntityType = AttachedEntityType.DISCOUNT_COUPON;
    }

    @Override
    public void setAttachedEntityType(AttachedEntityType attachedEntityType) {}

}
