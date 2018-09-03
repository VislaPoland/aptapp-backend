package com.creatix.service.notification;

import static java.util.stream.Collectors.toList;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.EscalatedNeighborhoodNotification;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.email.EscalatedManagerMessageForMoreTenantsTemplate;
import com.creatix.message.template.email.EscalatedManagerMessageTemplate;
import com.creatix.message.template.push.EscalatedManagerNotificationTemplate;
import com.creatix.message.template.sms.EscalatedManagerSmsTemplate;
import com.creatix.message.template.sms.EscalatedManagerSmsTemplateForMoreTenants;
import com.creatix.service.message.EmailMessageService;
import com.creatix.service.message.PushNotificationSender;
import freemarker.template.TemplateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.MessagingException;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Tomas Sedlak on 18.10.2017.
 */
class PropertyNotificationWatcher {

    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyNotificationWatcher.class);

    @Nonnull
    private final NotificationDao notificationDao;
    @Nonnull
    private final Long propertyId;
    @Nonnull
    private final boolean isThrottlingEnabled;
    @Nonnull
    private final PushNotificationSender pushNotificationSender;
    @Nonnull
    private final EmailMessageService emailMessageService;
    @Nonnull
    private final SmsMessageSender smsMessageSender;
    @Nonnull
    private final ApplicationProperties properties;

    @Nonnull
    private final ExpiringChannel<Neighbor, NeighborComplaint> disruptiveNeighborComplaints = new ExpiringChannel<>(Duration.ofHours(24));
    @Nonnull
    private final ExpiringChannel<NeighborRelation, NeighborComplaint> complaintThrottleFast = new ExpiringChannel<>(Duration.ofMinutes(15));
    @Nonnull
    private final ExpiringChannel<NeighborRelation, NeighborComplaint> complaintThrottleSlow = new ExpiringChannel<>(Duration.ofHours(24));
    @Nonnull
    private final ExpiringChannel<NeighborRelation, Escalation> lockoutLatch = new ExpiringChannel<>(Duration.ofHours(24));

    private final Object configurationSyncLock = new Object();
    private int disruptiveComplaintThreshold = 3;
    private int throttleFastLimit = 1;
    private int throttleSlowLimit = 3;


    PropertyNotificationWatcher(@Nonnull boolean isThrottlingEnabled, @Nonnull Property property, @Nonnull NotificationDao notificationDao, @Nonnull PushNotificationSender pushNotificationSender, @Nonnull EmailMessageService emailMessageService, @Nonnull SmsMessageSender smsMessageSender, @Nonnull ApplicationProperties properties) {
        this.isThrottlingEnabled = isThrottlingEnabled;
        this.notificationDao = notificationDao;
        this.propertyId = property.getId();
        this.pushNotificationSender = pushNotificationSender;
        this.emailMessageService = emailMessageService;
        this.smsMessageSender = smsMessageSender;
        this.properties = properties;
    }

    void processNotification(@Nonnull NeighborhoodNotification notification) {

        if ( (notification.getTargetApartment() == null) || (notification.getTargetApartment().getTenant() == null) ) {
            // do nothing
            return;
        }
        Property property = notification.getProperty();
        if ( property == null ) {
            // do nothing
            LOGGER.warn("Notification without property detected");
            return;
        }
        else {
            refreshConfiguration(property);
        }

        final Tenant accountOffender = notification.getTargetApartment().getTenant();
        final Tenant accountComplainer = (Tenant) notification.getAuthor();

        final NeighborRelation relation = new NeighborRelation(accountComplainer, accountOffender);

        final Blocking blocking = testIfShouldBlock(relation);
        if ( blocking.shouldBlock() && this.isThrottlingEnabled ) {
            throw new AccessDeniedException(blocking.getBlockingMessage());
        }
        else {
            final Neighbor offender = new Neighbor(accountOffender);
            final NeighborComplaint complaint = new NeighborComplaint(accountComplainer, notification.getTitle() + " \t-\t " + notification.getDescription() + " \t-\t [" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "]");

            complaintThrottleFast.put(relation, complaint);
            complaintThrottleSlow.put(relation, complaint);
            if ( disruptiveNeighborComplaints.get(offender).stream().noneMatch(c -> Objects.equals(c.getComplainerAccountId(), accountComplainer.getId())) ) {
                disruptiveNeighborComplaints.put(offender, complaint);
            }

            final boolean shouldEscalate = (complaintThrottleSlow.size(relation) >= getThrottleSlowLimit());
            final boolean shouldReportNeighbor = (disruptiveNeighborComplaints.size(offender) >= getDisruptiveComplaintThreshold());

            property.getManagers().forEach((PropertyManager manager) -> {

                // this will send notification to manager after one person complain to another max time per "day"
                if ( shouldEscalate ) {
                    sendEscalationNotification(accountOffender, accountComplainer, notification, manager);
                    lockoutLatch.put(relation, new Escalation());
                    sendEscalationEmail( property,
                                         accountOffender,
                                         (Tenant) notification.getAuthor(),
                                         manager,
                                         complaintThrottleSlow.get(relation).stream().map(NeighborComplaint::getComplainerMessage).collect(toList()) );
                    sendEscalationSms(property, manager, accountOffender.getApartment().getUnitNumber(), accountComplainer.getApartment().getUnitNumber());
                }

                // this will send email to manager when some people (depends on disruptive_complaint_threshold) will send notification to one person
                if ( shouldReportNeighbor ) {
                    sendEscalationEmailForMoreTenants( property,
                                                       accountOffender,
                                                       manager,
                                                       disruptiveNeighborComplaints.get(offender).stream().map(neighborComplaint -> neighborComplaint.getComplainerAppartmentUnit() + " \t-\t " +neighborComplaint.getComplainerMessage()).collect(toList()) );
                    sendEscalationSmsForMoreTenants(property, manager, accountOffender.getApartment().getUnitNumber());
                }
            });
        }
    }

    private void refreshConfiguration(@Nonnull Property property) {
        synchronized ( configurationSyncLock ) {
            throttleFastLimit = Optional.ofNullable(property.getThrottleFastLimit()).orElse(throttleFastLimit);
            throttleSlowLimit = Optional.ofNullable(property.getThrottleSlowLimit()).orElse(throttleSlowLimit);
            // TODO: fix this when the times is right. for now we will use same value as for max msg per person.
//            disruptiveComplaintThreshold = Optional.ofNullable(property.getDisruptiveComplaintThreshold()).orElse(disruptiveComplaintThreshold);
            disruptiveComplaintThreshold = Optional.ofNullable(property.getThrottleSlowLimit()).orElse(throttleSlowLimit);

        }

        complaintThrottleFast.setProtectedPeriod(Optional.ofNullable(property.getThrottleFastMinutes()).map(Duration::ofMinutes).orElse(complaintThrottleFast.getProtectedPeriod()));
        complaintThrottleSlow.setProtectedPeriod(Optional.ofNullable(property.getThrottleSlowHours()).map(Duration::ofHours).orElse(complaintThrottleSlow.getProtectedPeriod()));
        disruptiveNeighborComplaints.setProtectedPeriod(Optional.ofNullable(property.getDisruptiveComplaintHours()).map(Duration::ofHours).orElse(disruptiveNeighborComplaints.getProtectedPeriod()));
        lockoutLatch.setProtectedPeriod(Optional.ofNullable(property.getLockoutHours()).map(Duration::ofHours).orElse(lockoutLatch.getProtectedPeriod()));
    }

    private void sendEscalationNotification(@Nonnull Tenant offender, Tenant accountComplainer, @Nonnull NeighborhoodNotification notificationSrc, PropertyManager manager) {
        final EscalatedNeighborhoodNotification notificationDst = new EscalatedNeighborhoodNotification();
        notificationDst.setAuthor(accountComplainer);
        notificationDst.setProperty(notificationSrc.getProperty());
        notificationDst.setRecipient(manager);
        notificationDst.setDescription("The resident in unit " + offender.getApartment().getUnitNumber() + " has been sending multiple messages to unit " + accountComplainer.getApartment().getUnitNumber() + ".");
        notificationDst.setTitle("Apt. App Alert");
        notificationDst.setStatus(NotificationStatus.Pending);
        notificationDao.persist(notificationDst);

//        pushNotificationSender.sendNotification(new EscalatedNeighborNotificationTemplate(throttleSlowLimit, complaintThrottleSlow.getProtectedPeriod()), offender);
        try {
            pushNotificationSender.sendNotification(new EscalatedManagerNotificationTemplate(offender, notificationSrc.getAuthor(), throttleSlowLimit, complaintThrottleSlow.getProtectedPeriod()), manager);
        } catch (TemplateException | IOException e) {
            LOGGER.error("Unable to send escalation notification.", e);
        }
    }

    private void sendEscalationEmail(Property property, Tenant accountOffender, Tenant notificationAuthor, PropertyManager manager, List<String> neighborComplaints) {
        if (property.getEnableEmailEscalation() != null && property.getEnableEmailEscalation() == true) {
            try {
                emailMessageService.send(
                        new EscalatedManagerMessageTemplate(
                                accountOffender,
                                notificationAuthor,
                                manager,
                                properties,
                                neighborComplaints)
                );
            } catch (MessagingException | MessageDeliveryException | TemplateException | IOException e) {
                LOGGER.error("Unable to send escalation notification trough email.", e);
            }
        }
    }

    private void sendEscalationEmailForMoreTenants(Property property, Tenant accountOffender, PropertyManager manager, List<String> neighborComplaints) {
        if (property.getEnableEmailEscalation() != null && property.getEnableEmailEscalation() == true) {
            try {
                emailMessageService.send(
                        new EscalatedManagerMessageForMoreTenantsTemplate(
                                accountOffender,
                                manager,
                                properties,
                                neighborComplaints)
                );
            } catch (MessagingException | MessageDeliveryException | TemplateException | IOException e) {
                LOGGER.error("Unable to send escalation notification trough email.", e);
            }
        }
    }

    private void sendEscalationSms(Property property, PropertyManager manager, String unitNumber, String complainerUnit) {
        if (property.getEnableSmsEscalation() != null && property.getEnableSmsEscalation() == true && property.getEnableSms() == true) {
            try {
                if (manager.getPrimaryPhone() != null) {
                    smsMessageSender.send(new EscalatedManagerSmsTemplate(manager.getPrimaryPhone(), unitNumber, complainerUnit));
                }
            } catch (TemplateException | IOException | MessageDeliveryException e) {
                LOGGER.error("Unable to send escalation notification trough sms.", e);
            }
        }
    }

    private void sendEscalationSmsForMoreTenants(Property property, PropertyManager manager, String unitNumber) {
        if (property.getEnableSmsEscalation() != null && property.getEnableSmsEscalation() == true && property.getEnableSms() == true) {
            try {
                if (manager.getPrimaryPhone() != null) {
                    smsMessageSender.send(new EscalatedManagerSmsTemplateForMoreTenants(manager.getPrimaryPhone(), unitNumber));
                }
            } catch (TemplateException | IOException | MessageDeliveryException e) {
                LOGGER.error("Unable to send escalation notification trough sms.", e);
            }
        }
    }

    @Nonnull
    private Blocking testIfShouldBlock(@Nonnull NeighborRelation relation) {
        if ( lockoutLatch.size(relation) > 0 ) {
            Duration remainingTime = Duration.between(Instant.now(), lockoutLatch.nextOpenPeriod(relation).get());
            return Blocking.shouldBlock("Please try again in " + ( remainingTime.toHours() > 0 ?  remainingTime.toHours() + " hours." :
                                                                    ( remainingTime.toMinutes() > 0 ?  remainingTime.toMinutes() + " minutes." :
                                                                        remainingTime.getSeconds() + " seconds." )));
        }
        if ( complaintThrottleFast.size(relation) >= getThrottleFastLimit() ) {
            Duration remainingTime = Duration.between(Instant.now(), complaintThrottleFast.nextOpenPeriod(relation).get());
            return Blocking.shouldBlock("Please try again in " + ( remainingTime.toMinutes() > 0 ?  remainingTime.toMinutes() + " minutes." : remainingTime.getSeconds() + " seconds." ));
        }
        if ( complaintThrottleSlow.size(relation) >= getThrottleSlowLimit() ) {
            LOGGER.warn("Slow throttle was hit. This should not happen! Please check settings. property_id={}, lockout_count={}, lockout_period={}, tslow_count={}, tslow_period={}",
                    propertyId, lockoutLatch.size(relation), lockoutLatch.getProtectedPeriod(), complaintThrottleSlow.size(relation), complaintThrottleSlow.getProtectedPeriod());

            return Blocking.shouldBlock(String.format("Please try again in %d hours.", complaintThrottleSlow.getProtectedPeriod().getSeconds() / 3600));
        }

        return Blocking.shouldNotBlock();
    }

    private int getThrottleFastLimit() {
        synchronized ( configurationSyncLock ) {
            return throttleFastLimit;
        }
    }

    private int getThrottleSlowLimit() {
        synchronized ( configurationSyncLock ) {
            return throttleSlowLimit;
        }
    }

    private int getDisruptiveComplaintThreshold() {
        synchronized ( configurationSyncLock ) {
            return disruptiveComplaintThreshold;
        }
    }

    static class Blocking {
        private final boolean shouldBlock;
        private final String blockingMessage;

        private Blocking(boolean shouldBlock, String blockingMessage) {
            this.shouldBlock = shouldBlock;
            this.blockingMessage = blockingMessage;
        }

        @Nonnull
        static Blocking shouldBlock(@Nonnull String blockMessage) {
            return new Blocking(true, blockMessage);
        }

        @Nonnull
        static Blocking shouldNotBlock() {
            return new Blocking(false, null);
        }

        boolean shouldBlock() {
            return shouldBlock;
        }

        @Nullable
        String getBlockingMessage() {
            return blockingMessage;
        }
    }
}
