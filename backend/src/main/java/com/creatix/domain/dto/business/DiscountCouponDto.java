package com.creatix.domain.dto.business;

import lombok.Data;

/**
 * Created by kvimbi on 13/04/2017.
 */
@Data
public class DiscountCouponDto {

    private Long id;

    private boolean active = false;

    private int availableUses = 0;

    private String code;

}
