package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@ApiModel("Discount coupon")
@Data
public class DiscountCouponDto {

    @ApiModelProperty(value = "Id", readOnly = true, required = true)
    private Long id;

    @ApiModelProperty(value = "Whether discount coupon is enabled or not", required = true)
    private boolean enabled = false;

    @ApiModelProperty(value = "Predefined available uses", required = true, notes = "-1 for unlimited uses")
    private int availableUses = 0;

    @ApiModelProperty(value = "Coupon code used in QR")
    private String code;

}
