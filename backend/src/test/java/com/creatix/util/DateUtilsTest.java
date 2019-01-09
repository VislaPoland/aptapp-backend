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

import java.time.OffsetDateTime;

import static org.junit.Assert.*;

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
        assertEquals(from.toLocalDate().lengthOfMonth(),to.getDayOfMonth());
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
            dateUtils.assertRange(null, OffsetDateTime.now());
        } catch (AptValidationException ignored){

        }

        dateUtils.assertRange(OffsetDateTime.now(), null);
    }

    @Test
    public void shouldThrowWhenBadRange() throws AptValidationException {
        thrown.expect(new AptValidationExceptionMatcher("Start date has to be before end date of requested range."));

        dateUtils.assertRange(OffsetDateTime.now(), OffsetDateTime.now().minusDays(1));
    }

    @Test
    public void shouldPassWhenBothSet() throws AptValidationException {
        dateUtils.assertRange(OffsetDateTime.now(), OffsetDateTime.now().plusDays(1));
    }
}