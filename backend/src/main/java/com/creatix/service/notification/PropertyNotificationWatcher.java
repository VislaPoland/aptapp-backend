package com.creatix.service.notification;

import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.EscalatedNeighborhoodNotification;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.message.template.push.EscalatedManagerNotificationTemplate;
import com.creatix.message.template.push.EscalatedNeighborNotificationTemplate;
import com.creatix.service.message.PushNotificationSender;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Duration;
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
    private final PushNotificationSender pushNotificationSender;

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


    PropertyNotificationWatcher(@Nonnull Property property, @Nonnull NotificationDao notificationDao, @Nonnull PushNotificationSender pushNotificationSender) {
        this.notificationDao = notificationDao;
        this.propertyId = property.getId();
        this.pushNotificationSender = pushNotificationSender;
    }

    void processNotification(@Nonnull NeighborhoodNotification notification) throws IOException, TemplateException {

        if ( (notification.getTargetApartment() == null) || (notification.getTargetApartment().getTenant() == null) ) {
            // do nothing
            return;
        }
        if ( notification.getProperty() == null ) {
            // do nothing
            LOGGER.warn("Notification without property detected");
            return;
        }
        else {
            refreshConfiguration(notification.getProperty());
        }

        final Tenant accountOffender = notification.getTargetApartment().getTenant();
        final Account accountComplainer = notification.getAuthor();

        final NeighborRelation relation = new NeighborRelation(accountComplainer, accountOffender);

        final Blocking blocking = testIfShouldBlock(relation);
        if ( blocking.shouldBlock() ) {
            throw new AccessDeniedException(blocking.getBlockingMessage());
        }
        else {
            final Neighbor offender = new Neighbor(accountOffender);
            final NeighborComplaint complaint = new NeighborComplaint(accountComplainer);

            complaintThrottleFast.put(relation, complaint);
            complaintThrottleSlow.put(relation, complaint);
            if ( disruptiveNeighborComplaints.get(offender).stream().noneMatch(c -> Objects.equals(c.getComplainerAccountId(), accountComplainer.getId())) ) {
                disruptiveNeighborComplaints.put(offender, complaint);
            }


            final boolean shouldEscalate = (complaintThrottleSlow.size(relation) >= getThrottleSlowLimit());
            if ( shouldEscalate ) {
                sendEscalationNotification(accountOffender, notification);
                lockoutLatch.put(relation, new Escalation());
            }

            final boolean shouldReportNeighbor = (disruptiveNeighborComplaints.size(offender) >= getDisruptiveComplaintThreshold());
            if ( shouldReportNeighbor ) {
                sendDisruptiveNeighborNotification(accountOffender, accountComplainer);
            }
        }
    }

    private void refreshConfiguration(@Nonnull Property property) {
        synchronized ( configurationSyncLock ) {
            throttleFastLimit = Optional.ofNullable(property.getThrottleFastLimit()).orElse(throttleFastLimit);
            throttleSlowLimit = Optional.ofNullable(property.getThrottleSlowLimit()).orElse(throttleSlowLimit);
            disruptiveComplaintThreshold = Optional.ofNullable(property.getDisruptiveComplaintThreshold()).orElse(disruptiveComplaintThreshold);
        }

        complaintThrottleFast.setProtectedPeriod(Optional.ofNullable(property.getThrottleFastMinutes()).map(Duration::ofMinutes).orElse(complaintThrottleFast.getProtectedPeriod()));
        complaintThrottleSlow.setProtectedPeriod(Optional.ofNullable(property.getThrottleSlowHours()).map(Duration::ofHours).orElse(complaintThrottleSlow.getProtectedPeriod()));
        disruptiveNeighborComplaints.setProtectedPeriod(Optional.ofNullable(property.getDisruptiveComplaintHours()).map(Duration::ofHours).orElse(disruptiveNeighborComplaints.getProtectedPeriod()));
        lockoutLatch.setProtectedPeriod(Optional.ofNullable(property.getLockoutHours()).map(Duration::ofHours).orElse(lockoutLatch.getProtectedPeriod()));
    }

    private void sendDisruptiveNeighborNotification(@Nonnull Tenant offender, @Nonnull Account complainer) throws IOException, TemplateException {
        // TODO: notify admin and offender
    }

    private void sendEscalationNotification(@Nonnull Tenant offender, @Nonnull NeighborhoodNotification notificationSrc) throws IOException, TemplateException {
        final EscalatedNeighborhoodNotification notificationDst = new EscalatedNeighborhoodNotification();
        notificationDst.setAuthor(notificationSrc.getAuthor());
        notificationDst.setProperty(notificationSrc.getProperty());
        notificationDst.setTargetApartment(notificationSrc.getTargetApartment());
        notificationDst.setDescription(notificationSrc.getDescription());
        notificationDst.setTitle(notificationSrc.getTitle());
        notificationDst.setStatus(NotificationStatus.Pending);
        notificationDao.persist(notificationDst);

        pushNotificationSender.sendNotification(new EscalatedNeighborNotificationTemplate(throttleSlowLimit, complaintThrottleSlow.getProtectedPeriod()), offender);
        pushNotificationSender.sendNotification(new EscalatedManagerNotificationTemplate(offender, notificationSrc.getAuthor(), throttleSlowLimit, complaintThrottleSlow.getProtectedPeriod()), offender);
    }

    @Nonnull
    private Blocking testIfShouldBlock(@Nonnull NeighborRelation relation) {
        if ( lockoutLatch.size(relation) > 0 ) {
            return Blocking.shouldBlock(String.format("Please try again in %d hours.", lockoutLatch.getProtectedPeriod().getSeconds() / 3600));
        }
        if ( complaintThrottleFast.size(relation) >= getThrottleFastLimit() ) {
            return Blocking.shouldBlock(String.format("Please try again in %d minutes.", complaintThrottleFast.getProtectedPeriod().getSeconds() / 60));
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
