package com.creatix.domain.dto.notification;

import com.creatix.domain.dto.business.DiscountCouponDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Tomas Michalek on 30/06/2017.
 */
@ApiModel("Discount coupon notification")
@Data
@EqualsAndHashCode(callSuper = true)
public class DiscountCouponNotificationDto extends NotificationDto {

    @ApiModelProperty(value = "Discount coupon")
    private DiscountCouponDto discountCoupon;

}
