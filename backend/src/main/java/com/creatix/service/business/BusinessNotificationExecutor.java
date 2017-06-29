package com.creatix.service.business;

import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.TenantDao;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.entity.store.business.DiscountCoupon;
import com.creatix.domain.entity.store.notification.BusinessProfileNotification;
import com.creatix.domain.entity.store.notification.DiscountCouponNotification;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.message.PushNotificationTemplateProcessor;
import com.creatix.message.push.BusinessProfileCreatedPush;
import com.creatix.message.push.DiscountCouponCreatedPush;
import com.creatix.message.template.push.businessProfile.BusinessProfileCreatedTemplate;
import com.creatix.message.template.push.businessProfile.DiscountCouponCreatedTemplate;
import com.creatix.service.apartment.ApartmentService;
import com.creatix.service.message.PushNotificationService;
import freemarker.template.TemplateException;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Created by Tomas Michalek on 18/04/2017.
 */
@Service
public class BusinessNotificationExecutor {

    private final Logger logger = LoggerFactory.getLogger(BusinessNotificationExecutor.class);

    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private TenantDao tenantDao;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private PushNotificationTemplateProcessor templateProcessor;
    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private NotificationDao notificationDao;

    private BusinessProfileNotification storeBusinessProfileNotification(Account author, BusinessProfile businessProfile) {
        BusinessProfileNotification notification = new BusinessProfileNotification();
        notification.setCreatedAt(OffsetDateTime.now());
        notification.setUpdatedAt(OffsetDateTime.now());
        notification.setBusinessProfile(businessProfile);
        notification.setAuthor(author);
        notification.setTitle("New Business!");
        notification.setDescription("Hey, there is a new business opened in your area!");
        notification.setProperty(businessProfile.getProperty());
        notification.setStatus(NotificationStatus.Pending);
        notificationDao.persist(notification);
        return notification;
    }

    private DiscountCouponNotification storeDiscountCouponNotification(Account author, DiscountCoupon discountCoupon) {
        DiscountCouponNotification notification = new DiscountCouponNotification();
        notification.setCreatedAt(OffsetDateTime.now());
        notification.setUpdatedAt(OffsetDateTime.now());
        notification.setDiscountCoupon(discountCoupon);
        notification.setAuthor(author);
        notification.setTitle("New Coupon Available!");
        notification.setDescription("Hey, there is new discount coupon available!");
        notification.setProperty(discountCoupon.getBusinessProfile().getProperty());
        notification.setStatus(NotificationStatus.Pending);
        notificationDao.persist(notification);
        return notification;
    }

    @Async
    public void sendNotification(@NotNull  final BusinessProfile businessProfile){
        Objects.requireNonNull(businessProfile);

        final BusinessProfileCreatedPush notification = new BusinessProfileCreatedPush(businessProfile.getId());
        notification.setTitle("New Business!");
        try {
            notification.setMessage(
                    templateProcessor.processTemplate(new BusinessProfileCreatedTemplate(businessProfile))
            );
        } catch (IOException | TemplateException exception) {
            logger.error(
                    String.format(
                            "Unable to create template for push message about business profile %d",
                            businessProfile.getId()),
                    exception
            );
        }

        this.storeBusinessProfileNotification(businessProfile.getProperty().getOwner(), businessProfile);

        apartmentDao.findByProperty(businessProfile.getProperty())
                .stream()
                .flatMap(apartment -> tenantDao.listTenantsForApartment(apartment).stream())
                .parallel()
                .forEach(tenant -> {
                    try {
                        pushNotificationService.sendNotification(notification, tenant);
                    } catch (IOException exception) {
                        logger.error(
                                String.format(
                                        "Unable to create template for push message about business profile %d",
                                        businessProfile.getId()
                                ),
                                exception
                        );
                    }
                });
    }

    @Async
    public void sendNotification(final DiscountCoupon discountCoupon) {
        Objects.requireNonNull(discountCoupon);

        final DiscountCouponCreatedPush notification = new DiscountCouponCreatedPush(discountCoupon.getId());
        notification.setTitle("New Coupon Available!");
        try {
            notification.setMessage(
                    templateProcessor.processTemplate(new DiscountCouponCreatedTemplate(discountCoupon))
            );
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        storeDiscountCouponNotification(discountCoupon.getBusinessProfile().getProperty().getOwner(), discountCoupon);

        final BusinessProfile businessProfile = discountCoupon.getBusinessProfile();
        apartmentDao.findByProperty(businessProfile.getProperty())
                .stream()
                .flatMap(apartment -> tenantDao.listTenantsForApartment(apartment).stream())
                .parallel()
                .forEach(tenant -> {
                    try {
                        pushNotificationService.sendNotification(notification, tenant);
                    } catch (IOException exception) {
                        logger.error(
                                String.format(
                                        "Unable to send push notification about discount coupon %d to tenant %d",
                                        discountCoupon.getId(),
                                        tenant.getId()
                                ),
                                exception
                        );
                    }
                });
    }
}
