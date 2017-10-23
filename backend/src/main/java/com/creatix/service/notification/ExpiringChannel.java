package com.creatix.service.notification;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
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

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ScheduledFuture<?> schedule;
    private final Map<K, Queue<WrappedValue<V>>> queueMap = new HashMap<>();

    private final Object protectedPeriodSyncLock = new Object();
    private TemporalAmount protectedPeriod;

    public ExpiringChannel(TemporalAmount protectedPeriod) {
        this.protectedPeriod = protectedPeriod;
        schedule = scheduler.schedule(this::autoEvict, 1, TimeUnit.MINUTES);
    }

    void put(@Nonnull K key, @Nonnull V value) {
        synchronized ( queueMap ) {
            Queue<WrappedValue<V>> queue = queueMap.get(key);
            if ( queue == null ) {
                queue = new ArrayDeque<>();
                queueMap.put(key, queue);
            }
            queue.add(new WrappedValue<>(value, createEvictAfterTime()));
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

    private void autoEvict() {

        final Instant now = Instant.now();
        synchronized ( queueMap ) {
            queueMap.values().forEach(queue -> {
                for ( WrappedValue<V> wrappedValue = queue.peek(); wrappedValue != null; wrappedValue = queue.peek() ) {
                    if ( shouldEvict(wrappedValue, now) ) {
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

    void setProtectedPeriod(@Nonnull TemporalAmount protectedPeriod) {
        synchronized ( protectedPeriodSyncLock ) {
            this.protectedPeriod = protectedPeriod;
        }
    }

    @Nonnull
    public TemporalAmount getProtectedPeriod() {
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
        schedule.cancel(false);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }
}
