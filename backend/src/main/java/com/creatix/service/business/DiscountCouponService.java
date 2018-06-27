package com.creatix.service.business;

import com.creatix.domain.dao.business.BusinessProfileDao;
import com.creatix.domain.dao.business.DiscountCouponDao;
import com.creatix.domain.dao.business.DiscountCouponUsageDao;
import com.creatix.domain.dto.business.DiscountCouponDto;
import com.creatix.domain.entity.store.attachment.DiscountCouponPhoto;
import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.entity.store.business.DiscountCoupon;
import com.creatix.domain.entity.store.business.DiscountCouponUsage;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.AttachmentService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Service
@Transactional
public class DiscountCouponService {

    private final Logger logger = LoggerFactory.getLogger(BusinessNotificationExecutor.class);

    private ConcurrentMap<Long, Long> locks = new ConcurrentHashMap<>();

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
    @Autowired
    private AttachmentService attachmentService;

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
    public DiscountCoupon createDiscountCouponFromRequest(@NotNull DiscountCouponDto request, long businessProfileId) {
        Objects.requireNonNull(request, "Request object can not be null");

        BusinessProfile businessProfile = businessProfileDao.findById(businessProfileId);

        if (null == businessProfile) {
            throw new EntityNotFoundException(String.format("Business profile %d not found", businessProfileId));
        }

        if (authorizationManager.canWrite(businessProfile.getProperty())) {
            DiscountCoupon discountCoupon = businessMapper.toDiscountCoupon(request);
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
    public DiscountCoupon updateDiscountCouponFromRequest(@NotNull DiscountCouponDto request) {
        Objects.requireNonNull(request, "Request object can not be null");
        Objects.requireNonNull(request.getId(), "Coupon id can not be null for update");

        DiscountCoupon storedCoupon = findCouponById(request.getId());

        BusinessProfile businessProfile = storedCoupon.getBusinessProfile();
        if (authorizationManager.canWrite(businessProfile.getProperty())) {
            businessMapper.map(request, storedCoupon);
            //todo: Annotation on {@link DiscountCoupon} should handle this problem, but for some odd reason is not. Although cascade is not set changes from request are nevertheless propagated.
            storedCoupon.setBusinessProfile(businessProfile);
            discountCouponDao.persist(storedCoupon);
            return storedCoupon;
        }

        throw new SecurityException(
                String.format("You are not eligible to create or modify discount coupons for property %d",
                        businessProfile.getProperty().getId())
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
    @Transactional
    @NotNull
    @RoleSecured
    public DiscountCouponDto useDiscountCoupon(long discountCouponId) {
        synchronized (getLockObject(discountCouponId)) {
            try {
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
            } finally {
                locks.remove(discountCouponId);
            }
        }
    }

    private synchronized Object getLockObject(Long key) {
        locks.putIfAbsent(key, key);
        return locks.get(key);
    }

    private void removeLockObject(Long key) {
        locks.remove(key);
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
            logger.error("Unable to create QR code matrix data", e);
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
            logger.error("Unable to write image data to output stream", e);
        }
        return baos.toByteArray();

    }

    public DiscountCoupon storeDiscountCouponPhotos(MultipartFile[] files, long couponId) {
        final DiscountCoupon discountCoupon = findCouponById(couponId);

        if (discountCoupon.getDiscountCouponPhoto() != null) {
            final DiscountCouponPhoto discountCouponPhoto = discountCoupon.getDiscountCouponPhoto();
            discountCoupon.setDiscountCouponPhoto(null);
            discountCouponDao.persist(discountCoupon);
            attachmentService.deleteAttachment(discountCouponPhoto);
            // We need to flush delete command before new entry is inserted into database because of 1:1 relationship
            // between discount coupon and attachment.
            discountCouponDao.flush();
        }

        List<DiscountCouponPhoto> photoStoreList;
        try {
            photoStoreList = attachmentService.storeAttachments(files, foreignKeyObject -> {
                DiscountCouponPhoto discountCouponPhoto = new DiscountCouponPhoto();
                discountCouponPhoto.setDiscountCoupon(discountCoupon);
                return discountCouponPhoto;
            }, discountCoupon, DiscountCouponPhoto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to store photo for business profile", e);
        }
        if (null != photoStoreList && photoStoreList.size() == 1) {
            discountCoupon.setDiscountCouponPhoto(photoStoreList.get(0));
            discountCouponDao.persist(discountCoupon);
            return discountCoupon;
        } else {
            throw new IllegalArgumentException("Unable to store photo for business profile");
        }
    }

    public DiscountCoupon deleteCouponById(long couponId) {
        final DiscountCoupon discountCoupon = findCouponById(couponId);
        if (authorizationManager.canWrite(discountCoupon.getBusinessProfile().getProperty())) {
            discountCouponDao.delete(discountCoupon);
            return discountCoupon;
        }

        throw new SecurityException(
                String.format("You are not eligible to create or modify discount coupons for property %d",
                        discountCoupon.getBusinessProfile().getProperty().getId())
        );
    }
}
