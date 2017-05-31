package com.creatix.message.push;

/**
 * Created by Tomas Michalek on 18/04/2017.
 */
public class DiscountCouponCreatedPush extends GenericPushNotification {

    public static final String DISCOUNT_COUPON_ID_ATTRIBUTE_KEY = "discountCouponId";

    public DiscountCouponCreatedPush(long discountCouponId) {
        super();

        if ( ! this.getAttributes().containsKey(DISCOUNT_COUPON_ID_ATTRIBUTE_KEY)) {
            getAttributes().put(
                    DISCOUNT_COUPON_ID_ATTRIBUTE_KEY,
                    String.valueOf(discountCouponId)
            );
        }

    }

}
