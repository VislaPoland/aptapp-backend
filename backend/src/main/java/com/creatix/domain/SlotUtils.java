package com.creatix.domain;

import com.creatix.domain.entity.store.Slot;

import java.time.Duration;
import java.time.OffsetDateTime;

public class SlotUtils {

    public static int calculateUnitCount(OffsetDateTime beginDt, OffsetDateTime endDt, int unitDurationMinutes) {
        final Duration slotDuration = Duration.between(beginDt, endDt);
        return (int) (slotDuration.toMinutes() / unitDurationMinutes);
    }

    public static int calculateUnitCount(int durationMinutes, int unitDurationMinutes) {
        return (durationMinutes / unitDurationMinutes);
    }

    public static int calculateUnitOffset(Slot slot, OffsetDateTime unitTime) {
        final Duration unitOffset = Duration.between(slot.getBeginTime(), unitTime);
        final long offsetMinutes = unitOffset.toMinutes();
        if ( offsetMinutes < 0 ) {
            throw new IllegalArgumentException("Time is before slot begin time");
        }

        return (int) (offsetMinutes / slot.getUnitDurationMinutes());
    }

}
