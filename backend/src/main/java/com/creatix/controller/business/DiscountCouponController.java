package com.creatix.controller.business;

import com.creatix.domain.dto.business.DiscountCouponDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.business.DiscountCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@RestController
@RequestMapping("/api/properties/{propertyId}/businesses/{businessProfileId}/coupons")
public class DiscountCouponController {

    @Autowired
    private DiscountCouponService discountCouponService;
    @Autowired
    private BusinessMapper businessMapper;


    @RequestMapping(path = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public HttpEntity<DiscountCouponDto> createDiscountCoupon(
            @RequestBody @NotNull DiscountCouponDto request,
            @PathVariable("businessProfileId") Long businessProfileId) {
        return new HttpEntity<>(
                businessMapper.toDiscountCoupon(
                        discountCouponService.createDiscountCoupon(request, businessProfileId)
                )
        );
    }

    @RequestMapping(path = "/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public HttpEntity<DiscountCouponDto> updateDiscountCoupon(@RequestBody @NotNull DiscountCouponDto request) {
        return new HttpEntity<>(
                businessMapper.toDiscountCoupon(
                        discountCouponService.updateDiscountCoupon(request)
                )
        );
    }

    @RequestMapping(path = "/{couponId}", method = RequestMethod.GET)
    @RoleSecured
    public HttpEntity<DiscountCouponDto> getDiscountCoupon(@PathVariable("couponId") @NotNull Long couponId) {
        return new HttpEntity<>(businessMapper.toDiscountCoupon(discountCouponService.getById(couponId)));
    }

    @RequestMapping(path = "/{couponId}/qr", method = RequestMethod.GET)
    @RoleSecured
    public HttpEntity<byte[]> getCouponQRCode(@PathVariable("couponId") @NotNull Long couponId) {
        return new HttpEntity<>(discountCouponService.getCouponQR(couponId));
    }

    @RequestMapping(path = "/{couponId}/use", method = RequestMethod.PUT)
    @RoleSecured
    public HttpEntity<DiscountCouponDto> userDiscountCoupon(@PathVariable("couponId") @NotNull Long couponId) {
        return new HttpEntity<>(discountCouponService.useDiscountCoupon(couponId));
    }

}
