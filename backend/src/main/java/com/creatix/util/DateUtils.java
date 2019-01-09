package com.creatix.util;

import com.creatix.controller.exception.AptValidationException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Component
public class DateUtils {

    /**
     * return range left, right for current month
     *
     * @return range of dates
     */
    public Pair<OffsetDateTime, OffsetDateTime> getRangeForCurrentMonth() {
        // get range for current month
        OffsetDateTime now = OffsetDateTime.now();

        return new ImmutablePair<>(now.with(firstDayOfMonth()),now.with(lastDayOfMonth()));
    }

    /**
     * assert given range (left, right) is valid
     *
     * <p>
     *     <ul>
     *         <li>not null</li>
     *         <li>one of value not null</li>
     *         <li>from before to</li>
     *     </ul>
     * </p>
     *
     * @param from start of range
     * @param to end of range
     *
     * @throws com.creatix.controller.exception.AptValidationException when date range is invalid
     */
    public void assertRange(OffsetDateTime from, OffsetDateTime to) throws AptValidationException {
        if(from == null && to == null) {
            throw new AptValidationException("Both parameters (from, till) must be set.");
        }

        if (from == null ^ to == null) {
            throw new AptValidationException("Both parameters (from, till) must be set.");
        }

        if (to.isBefore(from)) {
            throw new AptValidationException("Start date has to be before end date of requested range.");
        }
    }
}
