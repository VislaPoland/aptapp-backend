package com.creatix.service.business;

import com.creatix.domain.dao.TenantDao;
import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.entity.store.business.DiscountCoupon;
import com.creatix.message.push.BusinessProfileCreatedPush;
import com.creatix.message.push.DiscountCouponCreatedPush;
import com.creatix.message.push.GenericPushNotification;
import com.creatix.service.message.PushNotificationService;
import com.creatix.message.PushNotificationTemplateProcessor;
import com.creatix.message.template.push.businessProfile.BusinessProfileCreatedTemplate;
import com.creatix.message.template.push.businessProfile.DiscountCouponCreatedTemplate;
import com.creatix.service.apartment.ApartmentService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Created by Tomas Michalek on 18/04/2017.
 */
@Service
public class BusinessNotificationExecutor {

    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private TenantDao tenantDao;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private PushNotificationTemplateProcessor templateProcessor;

    @Async
    public void sendNotification(@NotNull  final BusinessProfile businessProfile){
        Objects.requireNonNull(businessProfile);

        final BusinessProfileCreatedPush notification = new BusinessProfileCreatedPush(businessProfile.getId());
        try {
            notification.setMessage(
                    templateProcessor.processTemplate(new BusinessProfileCreatedTemplate(businessProfile))
            );
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        apartmentService.getApartmentsByPropertyId(businessProfile.getProperty().getId())
                .stream()
                .flatMap(apartment -> tenantDao.listTenantsForApartment(apartment).stream())
                .parallel()
                .forEach(tenant -> {
                    try {
                        pushNotificationService.sendNotification(notification, tenant);
                    } catch (IOException e) {
                        //TODO: handle this
                        e.printStackTrace();
                    }
                });
    }

    @Async
    public void sendNotification(final DiscountCoupon discountCoupon) {
        Objects.requireNonNull(discountCoupon);

        final DiscountCouponCreatedPush notification = new DiscountCouponCreatedPush(discountCoupon.getId());
        try {
            notification.setMessage(
                    templateProcessor.processTemplate(new DiscountCouponCreatedTemplate(discountCoupon))
            );
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        final BusinessProfile businessProfile = discountCoupon.getBusinessProfile();
        apartmentService.getApartmentsByPropertyId(businessProfile.getProperty().getId())
                .stream()
                .flatMap(apartment -> tenantDao.listTenantsForApartment(apartment).stream())
                .parallel()
                .forEach(tenant -> {
                    try {
                        pushNotificationService.sendNotification(notification, tenant);
                    } catch (IOException e) {
                        //TODO: handle this
                        e.printStackTrace();
                    }
                });
    }
}
