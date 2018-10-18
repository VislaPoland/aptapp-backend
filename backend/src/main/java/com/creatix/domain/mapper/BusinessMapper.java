package com.creatix.domain.mapper;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.business.BusinessCategoryDao;
import com.creatix.domain.dto.business.*;
import com.creatix.domain.entity.store.attachment.BusinessProfilePhoto;
import com.creatix.domain.entity.store.attachment.DiscountCouponPhoto;
import com.creatix.domain.entity.store.business.*;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@Component
public class BusinessMapper extends ConfigurableMapper {

    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private BusinessCategoryDao businessCategoryDao;
    @Autowired
    private PropertyDao propertyDao;

    @Override
    protected void configure(MapperFactory factory) {
        super.configure(factory);

        factory.classMap(BusinessProfile.class, BusinessProfileDto.class)
                .exclude("hasImage")
                .exclude("contact.id")
                .fieldAToB("property.id", "propertyId")
                .byDefault()
                .customize(new CustomMapper<BusinessProfile, BusinessProfileDto>() {
                    @Override
                    public void mapAtoB(BusinessProfile businessProfile, BusinessProfileDto businessProfileDto, MappingContext context) {
                        businessProfileDto.setImageUploaded((businessProfile.getBusinessProfilePhotoList() != null) && !(businessProfile.getBusinessProfilePhotoList().isEmpty()));
                        if (null != businessProfileDto.getContact() && null != businessProfile.getContact()) {
                            businessProfileDto.getContact().setId(businessProfile.getContact().getId());
                        }
                    }
                    @Override
                    public void mapBtoA(BusinessProfileDto businessProfileDto, BusinessProfile businessProfile, MappingContext context) {
                        businessProfile.setBusinessCategoryList(
                            businessProfileDto.getBusinessCategoryList().stream().map(
                                    c -> businessCategoryDao.findById(c.getId())
                            ).collect(Collectors.toSet())
                        );
                    }
                })
                .register();

        factory.classMap(BusinessProfileCreateRequest.class, BusinessProfile.class)
                .exclude("propertyId")
                .exclude("businessCategoryList")
                .exclude("shouldSentNotification")
                .byDefault()
                .customize(new CustomMapper<BusinessProfileCreateRequest, BusinessProfile>() {
                    @Override
                    public void mapAtoB(BusinessProfileCreateRequest businessProfileCreateRequest, BusinessProfile businessProfile, MappingContext context) {
                        if (null != businessProfileCreateRequest.getPropertyId()) {
                            businessProfile.setProperty(
                                    propertyDao.findById(businessProfileCreateRequest.getPropertyId())
                            );
                        }
                        businessProfile.setBusinessCategoryList(
                                businessProfileCreateRequest.getBusinessCategoryList().stream().map(
                                        c -> businessCategoryDao.findById(c.getId())
                                ).collect(Collectors.toSet())
                        );
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
        factory.classMap(DiscountCouponPhoto.class, DiscountCouponPhotoDto.class)
                .byDefault()
                .customize(new CustomMapper<DiscountCouponPhoto, DiscountCouponPhotoDto>() {
                    @Override
                    public void mapAtoB(DiscountCouponPhoto discountCouponPhoto, DiscountCouponPhotoDto discountCouponPhotoDto, MappingContext context) {
                        try {
                            discountCouponPhotoDto.setFileUrl(getDiscountCouponPhotoUrl(discountCouponPhoto));
                        } catch (MalformedURLException e) {
                            throw new IllegalStateException("Failed to create download URL", e);
                        }
                    }
                })
                .register();
        factory.classMap(DiscountCouponUsage.class, DiscountCouponDto.class)
                .customize(new CustomMapper<DiscountCouponUsage, DiscountCouponDto>() {
                    @Override
                    public void mapAtoB(DiscountCouponUsage discountCouponUsage, DiscountCouponDto discountCouponDto, MappingContext context) {
                        map(discountCouponUsage.getId().getDiscountCoupon(), discountCouponDto);
                        if ( authorizationManager.hasAnyOfRoles(AccountRole.Tenant, AccountRole.SubTenant) ) {
                            discountCouponDto.setAvailableUses(discountCouponUsage.getUsesLeft());
                        }
                    }

                    @Override
                    public void mapBtoA(DiscountCouponDto discountCouponDto, DiscountCouponUsage discountCouponUsage, MappingContext context) {
                        throw new UnsupportedOperationException("Conversion not allowed");
                    }
                })
                .register();

        factory.classMap(BusinessProfileCarteItem.class, BusinessProfileCarteItemDto.class)
                .byDefault()
                .customize(new CustomMapper<BusinessProfileCarteItem, BusinessProfileCarteItemDto>() {
                    @Override
                    public void mapAtoB(BusinessProfileCarteItem businessProfileCarteItem, BusinessProfileCarteItemDto businessProfileCarteItemDto, MappingContext context) {
                        try {
                            businessProfileCarteItemDto.setFileUrl(getCartPhotoUrl(businessProfileCarteItem));
                        } catch (MalformedURLException e) {
                            throw new IllegalStateException("Failed to create download URL", e);
                        }
                    }
                })
                .register();

        factory.classMap(BusinessProfilePhoto.class, BusinessProfilePhotoDto.class)
                .byDefault()
                .customize(new CustomMapper<BusinessProfilePhoto, BusinessProfilePhotoDto>() {
                    @Override
                    public void mapAtoB(BusinessProfilePhoto businessProfilePhoto, BusinessProfilePhotoDto businessProfilePhotoDto, MappingContext context) {
                        try {
                            businessProfilePhotoDto.setFileUrl(getCartPhotoUrl(businessProfilePhoto));
                        } catch (MalformedURLException e) {
                            throw new IllegalStateException("Failed to create download URL", e);
                        }
                    }
                })
                .register();
    }

    @NotNull
    private String getCartPhotoUrl(@NotNull BusinessProfileCarteItem businessProfileCarteItem) throws MalformedURLException {
        if (businessProfileCarteItem.getBusinessProfileCartePhoto() == null) {
            return null;
        }

        return applicationProperties.buildBackendUrl(
                String.format(
                        "api/attachments/%d/%s",
                        businessProfileCarteItem.getBusinessProfileCartePhoto().getId(),
                        businessProfileCarteItem.getBusinessProfileCartePhoto().getFileName()
                )
        ).toString();
    }
    @NotNull
    private String getCartPhotoUrl(@NotNull BusinessProfilePhoto businessProfilePhoto) throws MalformedURLException {
        return applicationProperties.buildBackendUrl(
                String.format(
                        "api/attachments/%d/%s",
                        businessProfilePhoto.getId(),
                        businessProfilePhoto.getFileName()
                )
        ).toString();
    }
    @NotNull
    private String getDiscountCouponPhotoUrl(@NotNull DiscountCouponPhoto discountCouponPhoto) throws MalformedURLException {
        return applicationProperties.buildBackendUrl(
                String.format(
                        "api/attachments/%d/%s",
                        discountCouponPhoto.getId(),
                        discountCouponPhoto.getFileName()
                )
        ).toString();
    }


    public BusinessProfileDto toBusinessProfile(@NotNull BusinessProfile businessProfile) {
        Objects.requireNonNull(businessProfile, "Business profile must not be null");
        return this.map(businessProfile, BusinessProfileDto.class);
    }

    public BusinessProfile toBusinessProfile(@NotNull BusinessProfileCreateRequest businessProfile) {
        Objects.requireNonNull(businessProfile, "Business profile request must not be null");
        return this.map(businessProfile, BusinessProfile.class);
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

    public BusinessProfileCarteItemDto toBusinessProfileCarteItem(@NotNull BusinessProfileCarteItem businessProfileCarteItem) {
        Objects.requireNonNull(businessProfileCarteItem);
        return this.map(businessProfileCarteItem, BusinessProfileCarteItemDto.class);
    }

    public BusinessProfileCarteItem toBusinessProfileCarteItem(@NotNull BusinessProfileCarteItemDto businessProfileCarteItemDto) {
        Objects.requireNonNull(businessProfileCarteItemDto);
        return this.map(businessProfileCarteItemDto, BusinessProfileCarteItem.class);
    }

    public BusinessProfilePhotoDto toBusinessProfilePhoto(@NotNull BusinessProfilePhoto businessProfilePhoto) {
        Objects.requireNonNull(businessProfilePhoto, "Profile photo object can not be null");
        return this.map(businessProfilePhoto, BusinessProfilePhotoDto.class);
    }
}
