package com.creatix.controller.v1.notifications;

import com.creatix.controller.exception.AptValidationException;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGlobalInfoDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGroupByAccountDto;
import com.creatix.domain.enums.NotificationType;
import com.creatix.mathers.AptValidationExceptionMatcher;
import com.creatix.service.notification.NotificationReportService;
import com.creatix.util.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(secure = false)
@WebMvcTest(NotificationReportingController.class)
public class NotificationReportingControllerTest {

    private static final String ENDPOINT_GET_MAINTENANCE = "/api/v1/notifications/1/reporting/maintenance";
    private static final String ENDPOINT_GET_MAINTENANCE_WITH_ARGS = "/api/v1/notifications/1/reporting/maintenance?from={from}&till={till}";
    private static final String ENDPOINT_GET_MAINTENANCE_WITH_ONE_ARG = "/api/v1/notifications/1/reporting/maintenance?from={from}";
    private static final String ENDPOINT_GET_MAINTENANCE_GLOBAL_INFO = "/api/v1/notifications/1/reporting/maintenance/global?from={from}&till={till}";
    private static final String ENDPOINT_GET_MAINTENANCE_GROUPED_BY_TECHNICIAN = "/api/v1/notifications/1/reporting/maintenance/technician?from={from}&till={till}";

    private static final String ENDPOINT_GET_NEIGHBORHOOD = "/api/v1/notifications/1/reporting/neighborhood";
    private static final String ENDPOINT_GET_NEIGHBORHOOD_WITH_ARGS = "/api/v1/notifications/1/reporting/neighborhood?from={from}&till={till}";
    private static final String ENDPOINT_GET_NEIGHBORHOOD_WITH_ONE_ARG = "/api/v1/notifications/1/reporting/neighborhood?from={from}";

    private static final String ENDPOINT_GET_SECURITY = "/api/v1/notifications/1/reporting/security";
    private static final String ENDPOINT_GET_SECURITY_WITH_ARGS = "/api/v1/notifications/1/reporting/security?from={from}&till={till}";
    private static final String ENDPOINT_GET_SECURITY_WITH_ONE_ARG = "/api/v1/notifications/1/reporting/security?from={from}";

    private static final String DESCRIPTION = "description";
    private static final String DATA = "$.data";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationReportService notificationReportService;

    @MockBean
    private DateUtils dateUtils;

    @SpyBean
    private NotificationReportingController notificationReportingController;

    @Captor
    private ArgumentCaptor<OffsetDateTime> offsetDateTimeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OffsetDateTime startDateTime = OffsetDateTime.of(2019, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC);
    private OffsetDateTime endDateTime = OffsetDateTime.of(2019, 1, 2, 1, 0, 0, 0, ZoneOffset.UTC);

    @Before
    public void setup() {
        assertNotNull(mockMvc);
        assertNotNull(notificationReportingController);
    }

    @Test
    public void shouldReturnMaintenanceNotificationsForCurrentMonth() throws Exception {
        testNotificationWithoutDatesForType(ENDPOINT_GET_MAINTENANCE, NotificationType.Maintenance, this.assertMaintenance(Assert::assertNull));
    }

    @Test
    public void shouldReturnMaintenanceNotificationForGivenRange() throws Exception {
        testNofificaitonWithDatesForType(ENDPOINT_GET_MAINTENANCE_WITH_ARGS, NotificationType.Maintenance, this.assertMaintenance(Assert::assertNotNull));
    }

    @Test
    public void shouldFailedMaintenanceWhenExceptionThrown() throws Exception {
        testFailNotificationForType(ENDPOINT_GET_MAINTENANCE_WITH_ONE_ARG, NotificationType.Maintenance);
    }

    @Test
    public void shouldReturnNeighborhoodNotificationsForCurrentMonth() throws Exception {
        testNotificationWithoutDatesForType(ENDPOINT_GET_NEIGHBORHOOD, NotificationType.Neighborhood, this.assertNeighborhood(Assert::assertNull));
    }

    @Test
    public void shouldReturnNeighborhoodNotificationForGivenRange() throws Exception {
        testNofificaitonWithDatesForType(ENDPOINT_GET_NEIGHBORHOOD_WITH_ARGS, NotificationType.Neighborhood, this.assertNeighborhood(Assert::assertNotNull));
    }

    @Test
    public void shouldFailedNeighborhoodWhenExceptionThrown() throws Exception {
        testFailNotificationForType(ENDPOINT_GET_NEIGHBORHOOD_WITH_ONE_ARG, NotificationType.Neighborhood);
    }

    @Test
    public void shouldReturnSecurityNotificationsForCurrentMonth() throws Exception {
        testNotificationWithoutDatesForType(ENDPOINT_GET_SECURITY, NotificationType.Security, this.assertSecurity(Assert::assertNull));
    }

    @Test
    public void shouldReturnSecurityNotificationForGivenRange() throws Exception {
        testNofificaitonWithDatesForType(ENDPOINT_GET_SECURITY_WITH_ARGS, NotificationType.Security, this.assertSecurity(Assert::assertNotNull));
    }

    @Test
    public void shouldFailedSecurityWhenExceptionThrown() throws Exception {
        testFailNotificationForType(ENDPOINT_GET_SECURITY_WITH_ONE_ARG, NotificationType.Security);
    }

    @Test
    public void shouldReturnGlobalInfoForMaintenance() throws Exception {
        when(notificationReportService.getGlobalStatistics(any(), any(), any(), any())).thenReturn(
                new NotificationReportGlobalInfoDto(1L, 1D, 1L, 1L, 1L)
        );
        doNothing().when(dateUtils).assertRange(any(), any());

        mockMvc.perform(get(ENDPOINT_GET_MAINTENANCE_GLOBAL_INFO, startDateTime, endDateTime))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(DATA.concat(".requests"), is(1)))
                .andExpect(jsonPath(DATA.concat(".openRequests"), is(1D)))
                .andExpect(jsonPath(DATA.concat(".pastDueDateRequests"), is(1)))
                .andExpect(jsonPath(DATA.concat(".averageTimeToConfirm"), is(1)))
                .andExpect(jsonPath(DATA.concat(".averageTimeToResolve"), is(1)));

        verify(dateUtils, times(0)).getRangeForCurrentMonth();
        verify(dateUtils).assertRange(any(), any());

        verify(notificationReportService).getGlobalStatistics(startDateTime, endDateTime, NotificationType.Maintenance, 1l);
    }

    @Test
    public void shouldReturnNotificationReportForTechnician() throws Exception {
        when(notificationReportService.getMaintenanceReportsGroupedByTechnician(any(), any(), any()))
                .thenReturn(Collections.singletonList(new NotificationReportGroupByAccountDto(1L, 1L, 1L, 1L)));
        doNothing().when(dateUtils).assertRange(any(), any());

        mockMvc.perform(get(ENDPOINT_GET_MAINTENANCE_GROUPED_BY_TECHNICIAN, startDateTime, endDateTime))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(DATA, hasSize(1))
                )
                .andExpect(jsonPath(DATA.concat("[0].confirmed"), is(1)))
                .andExpect(jsonPath(DATA.concat("[0].resolved"), is(1)))
                .andExpect(jsonPath(DATA.concat("[0].averageTimeToConfirm"), is(1)))
                .andExpect(jsonPath(DATA.concat("[0].averageTimeToResolve"), is(1)));

        verify(dateUtils, times(0)).getRangeForCurrentMonth();
        verify(dateUtils).assertRange(any(), any());

        verify(notificationReportService).getMaintenanceReportsGroupedByTechnician(startDateTime, endDateTime, 1L);
    }

    private void testNotificationWithoutDatesForType(String endpoint, NotificationType notificationType, Callable<Void> callable) throws Exception {
        mockDataForGETNotificationReport();

        doNothing().when(dateUtils).assertRange(any(), any());

        mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(DATA, hasSize(1))
                )
                .andExpect(
                        jsonPath(DATA.concat("[0].description"), is(DESCRIPTION))
                );

        callable.call();

        verify(dateUtils).getRangeForCurrentMonth();
        verify(dateUtils).assertRange(any(), any());

        // check if date was set to provide it into service
        verify(notificationReportService).getReportsByRange(startDateTime, endDateTime, notificationType, 1L);
    }

    private void testNofificaitonWithDatesForType(String endpoint, NotificationType notificationType, Callable<Void> callable) throws Exception {
        mockDataForGETNotificationReport();
        doNothing().when(dateUtils).assertRange(any(), any());

        mockMvc.perform(get(endpoint, startDateTime, endDateTime))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(DATA, hasSize(1))
                )
                .andExpect(
                        jsonPath(DATA.concat("[0].description"), is(DESCRIPTION))
                );

        callable.call();

        verify(dateUtils, times(0)).getRangeForCurrentMonth();
        verify(dateUtils).assertRange(any(), any());

        verify(notificationReportService).getReportsByRange(startDateTime, endDateTime, notificationType, 1L);
    }

    private void testFailNotificationForType(String endpoint, NotificationType notificationType) throws Exception {
        String message = "Both parameters (from, till) must be set.";
        doThrow(new AptValidationException(message)).when(dateUtils).assertRange(any(), any());
        thrown.expect(NestedServletException.class);
        thrown.expectCause(new AptValidationExceptionMatcher(message));

        mockMvc.perform(get(endpoint, startDateTime))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(dateUtils).assertRange(any(), any());
        verify(notificationReportService).getReportsByRange(any(), any(), notificationType, 1L);
    }

    private void mockDataForGETNotificationReport() {
        when(notificationReportService.getReportsByRange(any(), any(), any(), any())).thenReturn(Collections.singletonList(
                new NotificationReportDto(1L, "title", DESCRIPTION, startDateTime, endDateTime, 1L, 1L, "status")
        ));

        MaintenanceNotificationDto notificationDto = new MaintenanceNotificationDto();
        notificationDto.setDescription(DESCRIPTION);

        when(dateUtils.getRangeForCurrentMonth()).thenReturn(
                new ImmutablePair<>(startDateTime, endDateTime)
        );
    }

    private Callable<Void> assertMaintenance(Consumer<OffsetDateTime> assertFunction) {
        return () -> {
            verify(notificationReportingController).getMaintenanceNotificationsInDateRange(offsetDateTimeArgumentCaptor.capture(), offsetDateTimeArgumentCaptor.capture(), longArgumentCaptor.capture());

            List<OffsetDateTime> arguments = offsetDateTimeArgumentCaptor.getAllValues();
            arguments.forEach(assertFunction);

            assertEquals(1L, (long) longArgumentCaptor.getValue());

            return null;
        };
    }

    private Callable<Void> assertNeighborhood(Consumer<OffsetDateTime> assertFunction) {
        return () -> {
            verify(notificationReportingController).getNeighborhoodNotificationsInDateRange(offsetDateTimeArgumentCaptor.capture(), offsetDateTimeArgumentCaptor.capture(), longArgumentCaptor.capture());

            List<OffsetDateTime> arguments = offsetDateTimeArgumentCaptor.getAllValues();
            arguments.forEach(assertFunction);

            assertEquals(1L, (long) longArgumentCaptor.getValue());

            return null;
        };
    }

    private Callable<Void> assertSecurity(Consumer<OffsetDateTime> assertFunction) {
        return () -> {
            verify(notificationReportingController).getSecurityNotificationsInDateRange(offsetDateTimeArgumentCaptor.capture(), offsetDateTimeArgumentCaptor.capture(), longArgumentCaptor.capture());

            List<OffsetDateTime> arguments = offsetDateTimeArgumentCaptor.getAllValues();
            arguments.forEach(assertFunction);

            assertEquals(1L, (long) longArgumentCaptor.getValue());

            return null;
        };
    }
}