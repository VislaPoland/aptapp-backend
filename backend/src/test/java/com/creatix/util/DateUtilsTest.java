package com.creatix.util;

import com.creatix.controller.exception.AptValidationException;
import com.creatix.mathers.AptValidationExceptionMatcher;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class DateUtilsTest {

    @SpyBean
    private DateUtils dateUtils;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getRangeForCurrentMonth() {
        Pair<OffsetDateTime, OffsetDateTime> pair = dateUtils.getRangeForCurrentMonth();
        OffsetDateTime from = pair.getLeft();
        OffsetDateTime to = pair.getRight();

        assertNotNull(from);
        assertNotNull(to);
        assertEquals(from.getMonth(), to.getMonth());
        assertEquals(1, from.getDayOfMonth());
        assertEquals(from.toLocalDate().lengthOfMonth(), to.getDayOfMonth());

        //assert that time is set to 00:00:00
        assertEquals(0, from.getHour());
        assertEquals(0, from.getMinute());
        assertEquals(0, from.getSecond());

        //assert that time is set to 23:59:59
        assertEquals(23, to.getHour());
        assertEquals(59, to.getMinute());
        assertEquals(59, to.getSecond());
    }

    @Test
    public void shouldThrowWhenBothNull() throws AptValidationException {
        thrown.expect(new AptValidationExceptionMatcher("Both parameters (from, till) must be set."));

        dateUtils.assertRange(null, null);
    }

    @Test
    public void shouldThrowWhenAtLeastOneNull() throws AptValidationException {
        thrown.expect(new AptValidationExceptionMatcher("Both parameters (from, till) must be set."));

        try {
            dateUtils.assertRange(null, LocalDate.now());
        } catch (AptValidationException ignored) {

        }

        dateUtils.assertRange(LocalDate.now(), null);
    }

    @Test
    public void shouldThrowWhenBadRange() throws AptValidationException {
        thrown.expect(new AptValidationExceptionMatcher("Start date has to be before end date of requested range."));

        dateUtils.assertRange(LocalDate.now(), LocalDate.now().minusDays(1));
    }

    @Test
    public void shouldPassWhenBothSet() throws AptValidationException {
        dateUtils.assertRange(LocalDate.now(), LocalDate.now().plusDays(1));
    }

    @Test
    public void getRangeFromDates() {
        Pair<OffsetDateTime, OffsetDateTime> pair = dateUtils.getRangeFromDates(
                LocalDate.parse("2019-01-01"),
                LocalDate.parse("2019-01-30")
        );

        OffsetDateTime from = pair.getLeft();
        OffsetDateTime to = pair.getRight();

        assertNotNull(from);
        assertNotNull(to);

        assertEquals(1, from.getMonthValue());
        assertEquals(1, from.getDayOfMonth());
        assertEquals(2019, from.getYear());

        assertEquals(1, to.getMonthValue());
        assertEquals(30, to.getDayOfMonth());
        assertEquals(2019, to.getYear());

        //assert that time is set to 00:00:00
        assertEquals(0, from.getHour());
        assertEquals(0, from.getMinute());
        assertEquals(0, from.getSecond());

        //assert that time is set to 23:59:59
        assertEquals(23, to.getHour());
        assertEquals(59, to.getMinute());
        assertEquals(59, to.getSecond());
    }
}