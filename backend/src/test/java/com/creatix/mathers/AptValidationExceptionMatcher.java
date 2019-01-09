package com.creatix.mathers;

import com.creatix.controller.exception.AptValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

@RequiredArgsConstructor
public class AptValidationExceptionMatcher extends TypeSafeMatcher<AptValidationException> {

    private final String message;

    @Override
    protected boolean matchesSafely(AptValidationException item) {
        return StringUtils.equals(message, item.getMessage());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("expected message")
                .appendValue(message);
    }
}