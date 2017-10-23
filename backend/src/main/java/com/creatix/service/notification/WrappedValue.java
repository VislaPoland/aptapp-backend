package com.creatix.service.notification;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Created by Tomas Sedlak on 19.10.2017.
 */
class WrappedValue<T> {

    @Nonnull
    private final Instant evictAfter;
    @Nonnull
    private final T value;

    public WrappedValue(@Nonnull T value, @Nonnull Instant evictAfter) {
        this.value = value;
        this.evictAfter = evictAfter;
    }

    @Nonnull
    public Instant getEvictAfter() {
        return evictAfter;
    }

    @Nonnull
    public T getValue() {
        return value;
    }
}
