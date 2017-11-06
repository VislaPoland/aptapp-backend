package com.creatix.message.template.push.businessProfile;

import com.creatix.domain.entity.store.business.DiscountCoupon;
import com.creatix.message.template.push.PushMessageTemplate;

/**
 * Created by Tomas Michalek on 18/04/2017.
 */
public class DiscountCouponCreatedTemplate extends PushMessageTemplate {

    private final DiscountCoupon discountCoupon;

    public DiscountCouponCreatedTemplate(DiscountCoupon discountCoupon) {
        this.discountCoupon = discountCoupon;
    }

    @Override
    public String getTemplateName() {
        return "discount-coupon-created";
    }

}
