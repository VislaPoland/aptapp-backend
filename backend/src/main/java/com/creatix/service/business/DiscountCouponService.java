package com.creatix.service.business;

import com.creatix.domain.dao.business.BusinessProfileDao;
import com.creatix.domain.dao.business.DiscountCouponDao;
import com.creatix.domain.dao.business.DiscountCouponUsageDao;
import com.creatix.domain.dto.business.DiscountCouponDto;
import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.entity.store.business.DiscountCoupon;
import com.creatix.domain.entity.store.business.DiscountCouponUsage;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Service
public class DiscountCouponService {


    private final static int QR_CODE_SIZE = 128;

    @Autowired
    private DiscountCouponDao discountCouponDao;
    @Autowired
    private DiscountCouponUsageDao discountCouponUsageDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private BusinessProfileDao businessProfileDao;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private BusinessNotificationExecutor businessNotificationExecutor;

    /**
     * Finds discount coupon by id, or throws {@link EntityNotFoundException} if not found
     *
     * @param discountCouponId
     * @return
     */
    @NotNull
    public DiscountCoupon getById(long discountCouponId) {
        return findCouponById(discountCouponId);
    }

    @NotNull
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DiscountCoupon createDiscountCoupon(@NotNull DiscountCouponDto discountCouponDto, long businessProfileId) {
        Objects.requireNonNull(discountCouponDto);

        BusinessProfile businessProfile = businessProfileDao.findById(businessProfileId);

        if (null == businessProfile) {
            throw new EntityNotFoundException(String.format("Business profile %d not found", businessProfileId));
        }

        if (authorizationManager.canWrite(businessProfile.getProperty())) {
            DiscountCoupon discountCoupon = businessMapper.toDiscountCoupon(discountCouponDto);
            discountCoupon.setBusinessProfile(businessProfile);
            discountCouponDao.persist(discountCoupon);
            return discountCoupon;
        }

        throw new SecurityException(
                String.format("You are not eligible to create or modify discount coupons for property %d",
                        businessProfile.getProperty().getId())
        );
    }

    @NotNull
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DiscountCoupon updateDiscountCoupon(@NotNull DiscountCouponDto discountCouponDto) {
        Objects.requireNonNull(discountCouponDto);
        Objects.requireNonNull(discountCouponDto.getId());

        DiscountCoupon storedCoupon = findCouponById(discountCouponDto.getId());

        if (authorizationManager.canWrite(storedCoupon.getBusinessProfile().getProperty())) {
            businessMapper.map(discountCouponDto, storedCoupon);
            discountCouponDao.persist(storedCoupon);
            return storedCoupon;
        }

        throw new SecurityException(
                String.format("You are not eligible to create or modify discount coupons for property %d",
                        storedCoupon.getBusinessProfile().getProperty().getId())
        );
    }

    /**
     * Finds coupon usage for user currently authenticated user and specified coupon.
     * If not found creates new one with usage left specified by coupon configuration - 1
     * If found decrement uses if not set to {@link DiscountCoupon#UNLIMITED_USE}
     *
     * @param discountCouponId to lookup
     * @return discount coupon with decremented uses left
     */
    @NotNull
    @RoleSecured({AccountRole.Tenant, AccountRole.SubTenant})
    public DiscountCouponDto useDiscountCoupon(long discountCouponId) {
        DiscountCoupon discountCoupon = findCouponById(discountCouponId);

        authorizationManager.checkRead(discountCoupon.getBusinessProfile().getProperty());

        DiscountCouponUsage.IdKey usageKey = new DiscountCouponUsage.IdKey(
                authorizationManager.getCurrentAccount(),
                discountCoupon
        );
        DiscountCouponUsage couponUsage = discountCouponUsageDao.findById(usageKey);

        if (null == couponUsage) {
            couponUsage = new DiscountCouponUsage()
                    .setUsesLeft(
                            discountCoupon.getAvailableUses() == DiscountCoupon.UNLIMITED_USE ?
                                    DiscountCoupon.UNLIMITED_USE :
                                    discountCoupon.getAvailableUses() - 1
                    )
                    .setId(usageKey);
            discountCouponUsageDao.persist(couponUsage);
        } else {
            if (couponUsage.getUsesLeft() == 0) {
                throw new IllegalStateException(String.format("Coupon %d has no more uses left for user", discountCouponId));
            } else if (couponUsage.getUsesLeft() != DiscountCoupon.UNLIMITED_USE) {
                couponUsage.setUsesLeft(couponUsage.getUsesLeft() - 1);
                discountCouponUsageDao.persist(couponUsage);
            }
        }

        return businessMapper.toDiscountCoupon(couponUsage);
    }


    /**
     * Sends push notification about discount coupon to all tenants accommodated in property of the business
     * discount coupon belongs to
     *
     * @param discountCouponId
     */
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public void sendNotification(long discountCouponId) {
        final DiscountCoupon discountCoupon = findCouponById(discountCouponId);
        businessNotificationExecutor.sendNotification(discountCoupon);
    }

    /**
     * Returns {@link DiscountCoupon} or throws {@link EntityNotFoundException} if not found
     *
     * @param discountCouponId
     * @return discount coupon
     * @throws EntityNotFoundException if not found
     */
    private DiscountCoupon findCouponById(long discountCouponId) throws EntityNotFoundException {
        DiscountCoupon discountCoupon = discountCouponDao.findById(discountCouponId);

        if (null == discountCoupon) {
            throw new EntityNotFoundException(String.format("Coupon with id %d not found", discountCouponId));
        }

        return discountCoupon;
    }


    public byte[] getCouponQR(long discountCouponId) {
        DiscountCoupon coupon = getById(discountCouponId);
        if (null == coupon) {
            throw new EntityNotFoundException(String.format("Coupon with id %d not found", discountCouponId));
        }

        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
        hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = null;
        try {
            byteMatrix = qrCodeWriter.encode(coupon.getCode(), BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hintMap);
        } catch (WriterException e) {
            //todo: process this!
            e.printStackTrace();
        }
        int CrunchifyWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < CrunchifyWidth; i++) {
            for (int j = 0; j < CrunchifyWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            //TODO: handle this
            e.printStackTrace();
        }
        return baos.toByteArray();

    }

}
