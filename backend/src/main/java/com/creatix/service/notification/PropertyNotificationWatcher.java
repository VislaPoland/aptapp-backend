package com.creatix.service.notification;

import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.notification.EscalatedNeighborhoodNotification;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.enums.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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


    PropertyNotificationWatcher(@Nonnull NotificationDao notificationDao, @Nonnull Property property) {
        this.notificationDao = notificationDao;
        this.propertyId = property.getId();
    }

    void processNotification(@Nonnull NeighborhoodNotification notification) {

        if ( notification.getTargetApartment() == null ) {
            // do nothing
            return;
        }
        if ( notification.getProperty() == null ) {
            // do nothing
            return;
        }
        else {
            refreshConfiguration(notification.getProperty());
        }


        final NeighborRelation relation = new NeighborRelation(notification.getAuthor(), notification.getTargetApartment().getTenant());

        final Blocking blocking = testIfShouldBlock(relation);
        if ( blocking.shouldBlock() ) {
            throw new AccessDeniedException(blocking.getBlockingMessage());
        }
        else {
            final Neighbor offender = new Neighbor(notification.getTargetApartment().getTenant());
            final NeighborComplaint complaint = new NeighborComplaint(notification);

            complaintThrottleFast.put(relation, complaint);
            complaintThrottleSlow.put(relation, complaint);
            disruptiveNeighborComplaints.put(offender, complaint);


            final boolean shouldEscalate = (complaintThrottleSlow.size(relation) >= getThrottleSlowLimit());
            if ( shouldEscalate ) {
                final EscalatedNeighborhoodNotification escalationNotification = sendEscalationNotification(notification);
                lockoutLatch.put(relation, new Escalation(escalationNotification));
            }

            final boolean shouldReportNeighbor = (disruptiveNeighborComplaints.size(offender) >= getDisruptiveComplaintThreshold());
            if ( shouldReportNeighbor ) {
                sendDisruptiveNeighborNotification();
            }
        }
    }

    private void refreshConfiguration(@Nonnull Property property) {
        synchronized ( configurationSyncLock ) {
            throttleFastLimit = Optional.ofNullable(property.getThrottleFastLimit()).orElse(throttleFastLimit);
            throttleSlowLimit = Optional.ofNullable(property.getThrottleSlowLimit()).orElse(throttleSlowLimit);
            disruptiveComplaintThreshold = Optional.ofNullable(property.getDisruptiveComplaintThreshold()).orElse(disruptiveComplaintThreshold);
        }

        complaintThrottleFast.setProtectedPeriod(Duration.ofMinutes(Optional.ofNullable(property.getThrottleFastMinutes()).orElse((int) complaintThrottleFast.getProtectedPeriod().get(ChronoUnit.MINUTES))));
        complaintThrottleSlow.setProtectedPeriod(Duration.ofHours(Optional.ofNullable(property.getThrottleSlowHours()).orElse((int) complaintThrottleSlow.getProtectedPeriod().get(ChronoUnit.HOURS))));
        disruptiveNeighborComplaints.setProtectedPeriod(Duration.ofHours(Optional.ofNullable(property.getDisruptiveComplaintHours()).orElse((int) disruptiveNeighborComplaints.getProtectedPeriod().get(ChronoUnit.HOURS))));
        lockoutLatch.setProtectedPeriod(Duration.ofHours(Optional.ofNullable(property.getLockoutHours()).orElse((int) lockoutLatch.getProtectedPeriod().get(ChronoUnit.HOURS))));
    }

    private void sendDisruptiveNeighborNotification() {

    }

    @Nonnull
    private EscalatedNeighborhoodNotification sendEscalationNotification(@Nonnull NeighborhoodNotification notificationSrc) {
        final EscalatedNeighborhoodNotification notificationDst = new EscalatedNeighborhoodNotification();
        notificationDst.setAuthor(notificationSrc.getAuthor());
        notificationDst.setProperty(notificationSrc.getProperty());
        notificationDst.setTargetApartment(notificationSrc.getTargetApartment());
        notificationDst.setDescription(notificationSrc.getDescription());
        notificationDst.setTitle(notificationSrc.getTitle());
        notificationDst.setStatus(NotificationStatus.Pending);
        notificationDao.persist(notificationDst);

        // TODO: notify

        return notificationDst;
    }

    @Nonnull
    private Blocking testIfShouldBlock(@Nonnull NeighborRelation relation) {
        if ( lockoutLatch.size(relation) > 0 ) {
            return Blocking.shouldBlock(String.format("Please try again in %d hours.", lockoutLatch.getProtectedPeriod().get(ChronoUnit.HOURS)));
        }
        if ( complaintThrottleFast.size(relation) >= getThrottleFastLimit() ) {
            return Blocking.shouldBlock(String.format("Please try again in %d minutes.", complaintThrottleFast.getProtectedPeriod().get(ChronoUnit.MINUTES)));
        }
        if ( complaintThrottleSlow.size(relation) >= getThrottleSlowLimit() ) {
            LOGGER.warn("Slow throttle was hit. This should not happen! Please check settings. property_id={}, lockout_count={}, lockout_period={}, tslow_count={}, tslow_period={}",
                    propertyId, lockoutLatch.size(relation), lockoutLatch.getProtectedPeriod(), complaintThrottleSlow.size(relation), complaintThrottleSlow.getProtectedPeriod());

            return Blocking.shouldBlock(String.format("Please try again in %d hours.", complaintThrottleSlow.getProtectedPeriod().get(ChronoUnit.HOURS)));
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
