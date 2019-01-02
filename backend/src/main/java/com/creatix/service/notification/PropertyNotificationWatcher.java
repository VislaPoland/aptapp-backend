package com.creatix.service.notification;

import static java.util.stream.Collectors.toList;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.dao.NotificationGroupDao;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.account.TenantBase;
import com.creatix.domain.entity.store.notification.EscalatedNeighborhoodNotification;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.entity.store.notification.NotificationGroup;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.email.EscalatedManagerMessageForMoreTenantsTemplate;
import com.creatix.message.template.email.EscalatedManagerMessageTemplate;
import com.creatix.message.template.push.EscalatedManagerNotificationTemplate;
import com.creatix.message.template.push.EscalatedNeighborNotificationTemplate;
import com.creatix.message.template.sms.EscalatedManagerSmsTemplate;
import com.creatix.message.template.sms.EscalatedManagerSmsTemplateForMoreTenants;
import com.creatix.service.message.EmailMessageService;
import com.creatix.service.message.PushNotificationSender;
import com.creatix.util.StringUtils;

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
import java.util.stream.Collectors;

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
    private final NotificationGroupDao notificationGroupDao;

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


    PropertyNotificationWatcher(@Nonnull boolean isThrottlingEnabled, @Nonnull Property property, @Nonnull NotificationDao notificationDao, @Nonnull PushNotificationSender pushNotificationSender, @Nonnull EmailMessageService emailMessageService, @Nonnull SmsMessageSender smsMessageSender, @Nonnull ApplicationProperties properties, @Nonnull NotificationGroupDao notificationGroupDao) {
        this.isThrottlingEnabled = isThrottlingEnabled;
        this.notificationDao = notificationDao;
        this.propertyId = property.getId();
        this.pushNotificationSender = pushNotificationSender;
        this.emailMessageService = emailMessageService;
        this.smsMessageSender = smsMessageSender;
        this.properties = properties;
        this.notificationGroupDao = notificationGroupDao;
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
        final TenantBase accountComplainer = (TenantBase) notification.getAuthor();

        final NeighborRelation relation = new NeighborRelation(accountComplainer, accountOffender);

        final Blocking blocking = testIfShouldBlock(relation);
        if ( blocking.shouldBlock() && this.isThrottlingEnabled ) {
            throw new AccessDeniedException(blocking.getBlockingMessage());
        }
        else {
            final Neighbor offender = new Neighbor(accountOffender);
            final NeighborComplaint complaint = new NeighborComplaint(accountComplainer, StringUtils.translateTileFromEnumString(notification.getTitle()) + " \t-\t [" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "]");

            complaintThrottleFast.put(relation, complaint);
            complaintThrottleSlow.put(relation, complaint);
            int originalNumberOfNeighborComplaints = disruptiveNeighborComplaints.get(offender).size();
            if ( disruptiveNeighborComplaints.get(offender).stream().noneMatch(c -> Objects.equals(c.getComplainerAccountId(), accountComplainer.getId())) ) {
                disruptiveNeighborComplaints.put(offender, complaint);
            }

            final boolean shouldEscalate = (complaintThrottleSlow.size(relation) >= getThrottleSlowLimit());
            final boolean shouldReportNeighbor = ((disruptiveNeighborComplaints.size(offender) >= getDisruptiveComplaintThreshold()) && disruptiveNeighborComplaints.get(offender).size() != originalNumberOfNeighborComplaints);


            if ( shouldEscalate ) {
                sendEscalationNotificationToOffender(accountOffender, accountComplainer, notification);
                sendPushEscalationNotificationToOffender(accountOffender);

                lockoutLatch.put(relation, new Escalation());
            }

            NotificationGroup notificationGroup = new NotificationGroup();
            notificationGroupDao.persist(notificationGroup);

            property.getManagers().forEach((PropertyManager manager) -> {

                // this will send notification to manager after one person complain to another max time per "day"
                if ( shouldEscalate ) {
                    sendPushEscalationNotificationToManager(accountOffender.getApartment().getUnitNumber(), accountComplainer.getApartment().getUnitNumber(), manager);
                    sendEscalationEmail( property,
                                         accountOffender,
                                         (Tenant) notification.getAuthor(),
                                         manager,
                                         complaintThrottleSlow.get(relation).stream().map(NeighborComplaint::getComplainerMessage).collect(toList()) );
                    sendEscalationSms(property, manager, accountOffender.getApartment().getUnitNumber(), accountComplainer.getApartment().getUnitNumber());
                }

                // this will send email to manager when some people (depends on disruptive_complaint_threshold) will send notification to one person
                if ( shouldReportNeighbor ) {
                    sendEscalationNotificationToManager( accountOffender.getApartment().getUnitNumber(),
                                                         null,
                                                         notification,
                                                         manager,
                                                         disruptiveNeighborComplaints.get(offender).stream().map( neighborComplaint -> neighborComplaint.getComplainerApartmentUnit()).collect(Collectors.joining(", ")), notificationGroup);
                    sendPushEscalationNotificationToManager(accountOffender.getApartment().getUnitNumber(), null, manager);
                    sendEscalationEmailForMoreTenants( property,
                                                       accountOffender,
                                                       manager,
                                                       disruptiveNeighborComplaints.get(offender).stream().map(neighborComplaint -> neighborComplaint.getComplainerApartmentUnit() + " \t-\t " +neighborComplaint.getComplainerMessage()).collect(toList()) );
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
//        complaintThrottleSlow.setProtectedPeriod(Optional.ofNullable(property.getThrottleSlowHours()).map(Duration::ofHours).orElse(complaintThrottleSlow.getProtectedPeriod()));
        complaintThrottleSlow.setProtectedPeriod(Optional.ofNullable(property.getLockoutHours()).map(Duration::ofHours).orElse(complaintThrottleSlow.getProtectedPeriod()));

        disruptiveNeighborComplaints.setProtectedPeriod(Optional.ofNullable(property.getLockoutHours()).map(Duration::ofHours).orElse(complaintThrottleSlow.getProtectedPeriod()));
        lockoutLatch.setProtectedPeriod(Optional.ofNullable(property.getLockoutHours()).map(Duration::ofHours).orElse(lockoutLatch.getProtectedPeriod()));
    }

    private void sendEscalationNotificationToManager(@Nonnull String offenderUnitNuimber, Tenant accountComplainer, @Nonnull NeighborhoodNotification notificationSrc, PropertyManager manager, String units, NotificationGroup notificationGroup) {
        final EscalatedNeighborhoodNotification notificationManager = new EscalatedNeighborhoodNotification();
        notificationManager.setAuthor(accountComplainer == null ? manager : accountComplainer);
        notificationManager.setNotificationGroup(notificationGroup);
        notificationManager.setProperty(notificationSrc.getProperty());
        notificationManager.setRecipient(manager);
        String description = (accountComplainer != null)
                ? "The resident in unit " + accountComplainer.getApartment().getUnitNumber() + " has been sending multiple messages to unit " + offenderUnitNuimber + ". The manager has been notified."
                : "The resident in unit " + offenderUnitNuimber + " has received multiple messages from units: " + units + ".";
        notificationManager.setDescription(description);
        notificationManager.setTitle("Apt. App Alert");
        notificationManager.setStatus(NotificationStatus.Pending);
        notificationDao.persist(notificationManager);
    }

    private void sendEscalationNotificationToOffender(@Nonnull Tenant offender, TenantBase accountComplainer, @Nonnull NeighborhoodNotification notificationSrc) {
        final EscalatedNeighborhoodNotification notificationTenant = new EscalatedNeighborhoodNotification();
        notificationTenant.setAuthor(accountComplainer);
        notificationTenant.setProperty(notificationSrc.getProperty());
        notificationTenant.setRecipient(offender);
        notificationTenant.setTargetApartment(offender.getApartment());
        notificationTenant.setDescription("A neighbor notification was sent " + throttleSlowLimit + " times in " + lockoutLatch.getProtectedPeriod().toHours() + " hours. The manager has been notified.");
        notificationTenant.setTitle("Apt. App Alert");
        notificationTenant.setStatus(NotificationStatus.Pending);
        notificationDao.persist(notificationTenant);
    }

    private void sendPushEscalationNotificationToManager(@Nonnull String offenderUnit, String authorUnit, PropertyManager manager) {
        try {
            pushNotificationSender.sendNotification(new EscalatedManagerNotificationTemplate(offenderUnit, authorUnit), manager);
        } catch (TemplateException | IOException e) {
            LOGGER.error("Unable to send escalation notification.", e);
        }
    }

    private void sendPushEscalationNotificationToOffender(@Nonnull Tenant offender) {
        try {
            pushNotificationSender.sendNotification(new EscalatedNeighborNotificationTemplate(throttleSlowLimit, lockoutLatch.getProtectedPeriod()), offender);
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
            return Blocking.shouldBlock("The max number of notifications to your neighbor has been reached. Your manager has been notified and in "
                                            + ((remainingTime.toMinutes() > 0) ? ( remainingTime.toHours() + " hours and " + (remainingTime.toMinutes() - (remainingTime.toHours()*60)  ) + " minutes" ) : (remainingTime.getSeconds() < 0 ? 1 : remainingTime.getSeconds()) + " seconds" )
                                            + " you can notify your neighbor again.");
        }
        if ( complaintThrottleFast.size(relation) >= getThrottleFastLimit() ) {
            Duration remainingTime = Duration.between(Instant.now(), complaintThrottleFast.nextOpenPeriod(relation).get());
            return Blocking.shouldBlock("Please allow " + remainingTime.toMinutes() + " minutes and " + (remainingTime.getSeconds() < 0 ? 1 : remainingTime.getSeconds() - (remainingTime.toMinutes()*60)) + " seconds to pass to allow time for your neighbor to respond to your request.");
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
