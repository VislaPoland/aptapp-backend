package com.creatix.domain.dto.notification;

import com.creatix.domain.dto.business.BusinessProfileDto;
import com.creatix.domain.dto.business.DiscountCouponDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Tomas Michalek on 30/06/2017.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiscountCouponNotificationDto extends NotificationDto {

    private DiscountCouponDto discountCoupon;

    private BusinessProfileDto businessProfile;

}
