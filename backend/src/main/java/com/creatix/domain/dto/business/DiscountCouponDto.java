package com.creatix.domain.dto.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@ApiModel("Discount coupon")
@Data
public class DiscountCouponDto {

    @ApiModelProperty(value = "Id", readOnly = true, required = true)
    private Long id;

    @ApiModelProperty(value = "Whether discount coupon is enabled or not", required = true)
    @NotNull
    private boolean enabled = false;

    @ApiModelProperty(value = "Predefined available uses", required = true, notes = "-1 for unlimited uses")
    @NotNull
    @Min(-1L)
    private int availableUses = 0;

    @ApiModelProperty(value = "Coupon code used in QR")
    private String code;

    @ApiModelProperty("Title of discount coupon")
    @NotBlank
    private String title;

    @ApiModelProperty("Description of discount coupon")
    @NotBlank
    private String description;

    @ApiModelProperty(value = "Photo image for coupon")
    private DiscountCouponPhotoDto discountCouponPhoto;

}
