package com.creatix.domain.mapper;

import com.creatix.domain.dto.business.BusinessCategoryDto;
import com.creatix.domain.dto.business.BusinessContactDto;
import com.creatix.domain.dto.business.BusinessProfileDto;
import com.creatix.domain.dto.business.DiscountCouponDto;
import com.creatix.domain.entity.store.business.*;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@Component
public class BusinessMapper extends ConfigurableMapper {


    @Override
    protected void configure(MapperFactory factory) {
        super.configure(factory);

        factory.classMap(BusinessProfile.class, BusinessProfileDto.class)
                .exclude("hasImage")
                .byDefault()
                .customize(new CustomMapper<BusinessProfile, BusinessProfileDto>() {
                    @Override
                    public void mapAtoB(BusinessProfile businessProfile, BusinessProfileDto businessProfileDto, MappingContext context) {
                        businessProfileDto.setImageUploaded(businessProfile.isImageUploaded());
                    }
                })
                .register();
        factory.classMap(BusinessCategory.class, BusinessCategoryDto.class)
                .byDefault()
                .register();
        factory.classMap(BusinessContact.class, BusinessContactDto.class)
                .byDefault()
                .register();
        factory.classMap(DiscountCoupon.class, DiscountCouponDto.class)
                .byDefault()
                .register();
        factory.classMap(DiscountCouponUsage.class, DiscountCouponDto.class)
                .customize(new CustomMapper<DiscountCouponUsage, DiscountCouponDto>() {
                    @Override
                    public void mapAtoB(DiscountCouponUsage discountCouponUsage, DiscountCouponDto discountCouponDto, MappingContext context) {
                        map(discountCouponUsage.getId().getDiscountCoupon(), discountCouponDto);
                        discountCouponDto.setAvailableUses(discountCouponUsage.getUsesLeft());
                    }

                    @Override
                    public void mapBtoA(DiscountCouponDto discountCouponDto, DiscountCouponUsage discountCouponUsage, MappingContext context) {
                        throw new UnsupportedOperationException("Conversion not allowed");
                    }
                })
                .register();
    }


    public BusinessProfileDto toBusinessProfile(@NotNull BusinessProfile businessProfile) {
        Objects.requireNonNull(businessProfile, "Business profile must not be null");
        return this.map(businessProfile, BusinessProfileDto.class);
    }

    public BusinessProfile toBusinessProfile(@NotNull BusinessProfileDto businessProfileDto) {
        Objects.requireNonNull(businessProfileDto, "Business profile must not be null");
        return this.map(businessProfileDto, BusinessProfile.class);
    }

    public BusinessCategoryDto toBusinessCategory(@NotNull BusinessCategory businessCategory) {
        Objects.requireNonNull(businessCategory, "Business category must not be null");
        return this.map(businessCategory, BusinessCategoryDto.class);
    }

    public BusinessCategory toBusinessCategory(@NotNull BusinessCategoryDto businessCategoryDto) {
        Objects.requireNonNull(businessCategoryDto, "Business category must not be null");
        return this.map(businessCategoryDto, BusinessCategory.class);
    }

    public DiscountCouponDto toDiscountCoupon(@NotNull DiscountCoupon discountCoupon) {
        Objects.requireNonNull(discountCoupon, "Discount coupon must not be null");
        return this.map(discountCoupon, DiscountCouponDto.class);
    }

    public DiscountCoupon toDiscountCoupon(@NotNull DiscountCouponDto discountCouponDto) {
        Objects.requireNonNull(discountCouponDto, "Discount coupon must not be null");
        return this.map(discountCouponDto, DiscountCoupon.class);
    }

    public DiscountCouponDto toDiscountCoupon(@NotNull DiscountCouponUsage discountCouponUsage) {
        Objects.requireNonNull(discountCouponUsage);
        return this.map(discountCouponUsage, DiscountCouponDto.class);
    }

}
