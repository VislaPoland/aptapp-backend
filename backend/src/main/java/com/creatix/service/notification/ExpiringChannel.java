package com.creatix.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Tomas Sedlak on 19.10.2017.
 */
class ExpiringChannel<K, V> implements AutoCloseable {

    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiringChannel.class);

    @Nonnull
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Nonnull
    private final ScheduledFuture<?> schedule;
    @Nonnull
    private final Map<K, Queue<WrappedValue<V>>> queueMap = new HashMap<>();

    @Nonnull
    private final Object protectedPeriodSyncLock = new Object();
    @Nonnull
    private Duration protectedPeriod;

    ExpiringChannel(@Nonnull Duration protectedPeriod) {
        LOGGER.trace("Creating new instance with protected period: {}", protectedPeriod);
        this.protectedPeriod = protectedPeriod;
        schedule = scheduler.scheduleAtFixedRate(this::autoEvict, 5, 5, TimeUnit.SECONDS);
    }

    void put(@Nonnull K key, @Nonnull V value) {
        synchronized ( queueMap ) {
            Queue<WrappedValue<V>> queue = queueMap.get(key);
            if ( queue == null ) {
                queue = new ArrayDeque<>();
                queueMap.put(key, queue);
            }
            final WrappedValue<V> wrappedValue = new WrappedValue<>(value, createEvictAfterTime());
            LOGGER.trace("Put: {}", wrappedValue);
            queue.add(wrappedValue);
        }
    }

    int size(@Nonnull K key) {
        synchronized ( queueMap ) {
            final Queue<WrappedValue<V>> queue = queueMap.get(key);
            if ( (queue == null) || queue.isEmpty() ) {
                return 0;
            }
            else {
                return queue.size();
            }
        }
    }

    @Nonnull
    Optional<V> peek(@Nonnull K key) {
        synchronized ( queueMap ) {
            final Queue<WrappedValue<V>> queue = queueMap.get(key);
            if ( queue == null ) {
                return Optional.empty();
            }
            else if ( queue.isEmpty() ) {
                return Optional.empty();
            }
            else {
                return Optional.of(queue.peek().getValue());
            }
        }
    }

    @Nonnull
    List<V> get(@Nonnull K key) {
        synchronized ( queueMap ) {
            final Queue<WrappedValue<V>> queue = queueMap.get(key);
            if ( queue == null ) {
                return Collections.emptyList();
            }
            else {
                return queue.stream().map(WrappedValue::getValue).collect(Collectors.toList());
            }
        }
    }

    @Nonnull
    Optional<Instant> nextOpenPeriod(@Nonnull K key) {
        synchronized ( queueMap ) {
            final Queue<WrappedValue<V>> queue = queueMap.get(key);
            if ( queue == null ) {
                return Optional.empty();
            }
            else if ( queue.isEmpty() ) {
                return Optional.empty();
            }
            else {
                return Optional.of(queue.peek().getEvictAfter());
            }
        }
    }

    private void autoEvict() {
        LOGGER.trace("Running auto evict");

        final Instant now = Instant.now();
        synchronized ( queueMap ) {
            LOGGER.trace("Queue size: {}", queueMap.size());
            queueMap.values().forEach(queue -> {
                for ( WrappedValue<V> wrappedValue = queue.peek(); wrappedValue != null; wrappedValue = queue.peek() ) {
                    if ( shouldEvict(wrappedValue, now) ) {
                        LOGGER.trace("Evict: {}", wrappedValue);
                        queue.remove();
                    }
                    else {
                        // nothing to evict
                        break;
                    }
                }
            });
        }
    }

    void setProtectedPeriod(@Nonnull Duration protectedPeriod) {
        LOGGER.trace("Protected period: {}", protectedPeriod);
        synchronized ( protectedPeriodSyncLock ) {
            this.protectedPeriod = protectedPeriod;
        }
    }

    @Nonnull
    public Duration getProtectedPeriod() {
        synchronized ( protectedPeriodSyncLock ) {
            return protectedPeriod;
        }
    }

    @Nonnull
    private Instant createEvictAfterTime() {
        synchronized ( protectedPeriodSyncLock ) {
            return Instant.now().plus(protectedPeriod);
        }
    }

    private boolean shouldEvict(@Nonnull WrappedValue<?> wrappedValue, @Nonnull Instant now) {
        return wrappedValue.getEvictAfter().isBefore(now);
    }

    @Override
    public void close() throws Exception {
        LOGGER.trace("Cancelling schedule");
        schedule.cancel(false);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }
}
