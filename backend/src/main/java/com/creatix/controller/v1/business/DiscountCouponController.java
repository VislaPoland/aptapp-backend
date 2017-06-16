package com.creatix.controller.v1.business;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.business.BusinessProfileDto;
import com.creatix.domain.dto.business.DiscountCouponDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ApplicationFeatureType;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.business.BusinessProfileService;
import com.creatix.service.business.DiscountCouponService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@RestController
@RequestMapping(path = {"/api/properties/{propertyId}/businesses/{businessProfileId}/coupons", "/api/v1/properties/{propertyId}/businesses/{businessProfileId}/coupons"})
@ApiVersion(1.0)
public class DiscountCouponController {

    @Autowired
    private DiscountCouponService discountCouponService;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private BusinessProfileService businessProfileService;


    @ApiOperation(value = "List business discount coupons")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public DataResponse<List<DiscountCouponDto>> listDiscountCoupons(@PathVariable("businessProfileId") Long businessProfileId) {
        return new DataResponse<>(
                businessProfileService.listBusinessDiscountCoupons(businessProfileId)
                        .stream()
                        .map(dc -> businessMapper.toDiscountCoupon(dc))
                        .collect(Collectors.toList())
        );
    }

    @ApiOperation("Creates new discount coupon")
    @RequestMapping(path = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public HttpEntity<DiscountCouponDto> createDiscountCoupon(
            @RequestBody @NotNull @Valid DiscountCouponDto request,
            @PathVariable("businessProfileId") Long businessProfileId) {
        return new HttpEntity<>(
                businessMapper.toDiscountCoupon(
                        discountCouponService.createDiscountCouponFromRequest(request, businessProfileId)
                )
        );
    }

    @ApiOperation("Updates existing discount coupon")
    @RequestMapping(path = "/{couponId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public HttpEntity<DiscountCouponDto> updateDiscountCoupon(@RequestBody @Valid @NotNull DiscountCouponDto request,
                                                              @PathVariable("couponId") Long couponId) {
        return new HttpEntity<>(
                businessMapper.toDiscountCoupon(
                        discountCouponService.updateDiscountCouponFromRequest(request)
                )
        );
    }

    @ApiOperation("Deletes existing discount coupon")
    @RequestMapping(path = "/{couponId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public HttpEntity<DiscountCouponDto> deleteDiscountCoupon(@PathVariable("couponId") Long couponId) {
        return new HttpEntity<>(
                businessMapper.toDiscountCoupon(
                        discountCouponService.deleteCouponById(couponId)
                )
        );
    }

    @ApiOperation("Returns single discount coupon")
    @RequestMapping(path = "/{couponId}", method = RequestMethod.GET)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public HttpEntity<DiscountCouponDto> getDiscountCoupon(@PathVariable("couponId") @NotNull Long couponId) {
        return new HttpEntity<>(businessMapper.toDiscountCoupon(discountCouponService.getById(couponId)));
    }

    @ApiOperation("Sends notification about discount coupon")
    @RequestMapping(path = "/{couponId}/sendNotification", method = RequestMethod.GET)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public void sendNotification(@PathVariable("couponId") @NotNull Long couponId) {
        discountCouponService.sendNotification(couponId);
    }

    @ApiOperation("Download qr code for discount coupon")
    @RequestMapping(path = "/{couponId}/qr", method = RequestMethod.GET)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public HttpEntity<byte[]> getCouponQRCode(@PathVariable("couponId") @NotNull Long couponId) {
        return new HttpEntity<>(discountCouponService.getCouponQR(couponId));
    }

    @ApiOperation("Use discount coupon")
    @RequestMapping(path = "/{couponId}/use", method = RequestMethod.PUT)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public HttpEntity<DiscountCouponDto> userDiscountCoupon(@PathVariable("couponId") @NotNull Long couponId) {
        return new HttpEntity<>(discountCouponService.useDiscountCoupon(couponId));
    }


    @ApiOperation(value = "Upload coupon photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{couponId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<DiscountCouponDto> storeBusinessProfilePhotos(@RequestParam MultipartFile[] files, @PathVariable long couponId) throws IOException {
        return new DataResponse<>(
                businessMapper.toDiscountCoupon(discountCouponService.storeDiscountCouponPhotos(files, couponId))
        );
    }

}
