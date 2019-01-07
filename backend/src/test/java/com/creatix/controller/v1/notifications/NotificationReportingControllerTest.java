package com.creatix.controller.v1.notifications;

import com.creatix.controller.exception.AptValidationException;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(secure = false)
@WebMvcTest(NotificationReportingController.class)
public class NotificationReportingControllerTest {

    private static final String ENDPOINT_GET_MAINTENANCE = "/api/v1/notifications/reporting/maintenance";
    private static final String ENDPOINT_GET_MAINTENANCE_WITH_ARGS = "/api/v1/notifications/reporting/maintenance?from={from}&till={till}";
    private static final String ENDPOINT_GET_MAINTENANCE_WITH_ONE_ARG = "/api/v1/notifications/reporting/maintenance?from={from}";
    private static final String DESCRIPTION = "description";
    private static final String DATA = "$.data";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private Mapper mapper;

    @SpyBean
    private NotificationReportingController notificationReportingController;

    @Captor
    private ArgumentCaptor<OffsetDateTime> offsetDateTimeArgumentCaptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OffsetDateTime startDateTime = OffsetDateTime.of(2019, 1, 1,1,0,0,0, ZoneOffset.UTC);
    private OffsetDateTime endDateTime = OffsetDateTime.of(2019, 1, 2,1,0,0,0, ZoneOffset.UTC);

    @Before
    public void setup() {
        assertNotNull(mockMvc);
        assertNotNull(notificationReportingController);
    }

    @Test
    public void shouldReturnMaintenanceNotificationsForCurrentMonth() throws Exception {
        mockDataForGETMaintenance();

        mockMvc.perform(get(ENDPOINT_GET_MAINTENANCE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(DATA, hasSize(1))
                )
                .andExpect(
                        jsonPath(DATA.concat("[0].description"), is(DESCRIPTION))
                );


        verify(notificationReportingController).getMaintenanceNotificationsInDateRange(offsetDateTimeArgumentCaptor.capture(), offsetDateTimeArgumentCaptor.capture());

        List<OffsetDateTime> arguments = offsetDateTimeArgumentCaptor.getAllValues();
        arguments.forEach(Assert::assertNull);

        verify(notificationService).getAllMaintenanceNotificationsInDateRange(any(), any());
        verify(mapper).toMaintenanceNotificationDto(any());
    }

    @Test
    public void shouldReturnMaintenanceNotificationForGivenRange() throws Exception {
        mockDataForGETMaintenance();

        mockMvc.perform(get(ENDPOINT_GET_MAINTENANCE_WITH_ARGS, startDateTime.toString(), endDateTime.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(DATA, hasSize(1))
                )
                .andExpect(
                        jsonPath(DATA.concat("[0].description"), is(DESCRIPTION))
                );

        verify(notificationReportingController).getMaintenanceNotificationsInDateRange(offsetDateTimeArgumentCaptor.capture(), offsetDateTimeArgumentCaptor.capture());

        List<OffsetDateTime> arguments = offsetDateTimeArgumentCaptor.getAllValues();
        arguments.forEach(Assert::assertNotNull);

        verify(notificationService).getAllMaintenanceNotificationsInDateRange(startDateTime, endDateTime);
        verify(mapper).toMaintenanceNotificationDto(any());
    }

    @Test
    public void shouldFailedWhenNotBothRangeValuesGiven() throws Exception {
        thrown.expect(NestedServletException.class);
        thrown.expectCause(new AptValidationExceptionMatcher("Both parameters (from, till) must be set or empty."));

        mockMvc.perform(get(ENDPOINT_GET_MAINTENANCE_WITH_ONE_ARG, startDateTime))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailedWhenRangeIsInvalid() throws Exception {
        thrown.expect(AptValidationException.class);
        thrown.expectCause(new AptValidationExceptionMatcher("Start date has to be before end date of requested range."));

        mockMvc.perform(get(ENDPOINT_GET_MAINTENANCE_WITH_ARGS, endDateTime, startDateTime))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void mockDataForGETMaintenance() {
        when(notificationService.getAllMaintenanceNotificationsInDateRange(any(), any())).thenReturn(Collections.singletonList(
                new MaintenanceNotification()
        ));

        MaintenanceNotificationDto notificationDto = new MaintenanceNotificationDto();
        notificationDto.setDescription(DESCRIPTION);

        when(mapper.toMaintenanceNotificationDto(any())).thenReturn(notificationDto);
    }

    @RequiredArgsConstructor
    private static class AptValidationExceptionMatcher extends TypeSafeMatcher<AptValidationException> {

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
}