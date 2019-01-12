package com.creatix.service.notification;

import com.creatix.controller.exception.AptValidationException;
import com.creatix.domain.dao.notifications.NotificationReportDao;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationType;
import com.creatix.mathers.AptValidationExceptionMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class NotificationReportServiceTest {

    private static final long PROPERTY_ID = 1L;
    @MockBean
    private NotificationReportDao notificationReportDao;

    @SpyBean
    private NotificationReportService notificationReportService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final OffsetDateTime OFFSET_DATE_TIME_MOCK = OffsetDateTime.now();

    @Test
    public void getReportsByRange() {
        when(notificationReportDao.getNotificationReport(any(), any(), any(), any())).thenReturn(null);

        notificationReportService.getReportsByRange(
                OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, NotificationType.Maintenance, PROPERTY_ID);

        verify(notificationReportDao).getNotificationReport(
                OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, NotificationType.Maintenance, PROPERTY_ID);
    }

    @Test
    public void getMaintenanceReportsGroupedByTechnician() {
        when(notificationReportDao.getNotificationReportGroupedByAccount(any(), any(), any(), any(), any())).thenReturn(null);

        notificationReportService.getMaintenanceReportsGroupedByTechnician(
                OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, PROPERTY_ID);

        verify(notificationReportDao).getNotificationReportGroupedByAccount(
                OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK,
                NotificationType.Maintenance, AccountRole.Maintenance, PROPERTY_ID);
    }

    @Test
    public void getGlobalStatistics() throws AptValidationException {
        when(notificationReportDao.getMaintenanceGlobalInfo(any(), any(), any())).thenReturn(null);

        notificationReportService.getGlobalStatistics(OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK,
                NotificationType.Maintenance, PROPERTY_ID);

        verify(notificationReportDao).getMaintenanceGlobalInfo(OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK,
                PROPERTY_ID);
    }

    /**
     * should only accept {@link NotificationType#Maintenance} otherwise throw exception
     * @throws AptValidationException if unsupported {@link NotificationType} is provided
     */
    @Test
    public void unsupportedTypeForGlobalStatistics() throws AptValidationException {
        thrown.expect(new AptValidationExceptionMatcher("Unable to get global statistics. Unsupported notification type Security"));

        notificationReportService.getGlobalStatistics(OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, NotificationType.Security, PROPERTY_ID);

        verify(notificationReportDao, times(0)).getMaintenanceGlobalInfo(any(), any(), any());
    }
}