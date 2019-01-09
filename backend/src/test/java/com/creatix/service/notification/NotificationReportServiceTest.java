package com.creatix.service.notification;

import com.creatix.domain.dao.notifications.NotificationReportDao;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class NotificationReportServiceTest {

    @MockBean
    private NotificationReportDao notificationReportDao;

    @SpyBean
    private NotificationReportService notificationReportService;

    private static final OffsetDateTime OFFSET_DATE_TIME_MOCK = OffsetDateTime.now();

    @Test
    public void getReportsByRange() {
        when(notificationReportDao.getNotificationReport(any(), any(), any())).thenReturn(null);

        notificationReportService.getReportsByRange(OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, NotificationType.Maintenance);

        verify(notificationReportDao).getNotificationReport(OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, NotificationType.Maintenance);
    }

    @Test
    public void getMaintenanceReportsGroupedByTechnician() {
        when(notificationReportDao.getNotificationReportGroupedByAccount(any(), any(), any(), any())).thenReturn(null);

        notificationReportService.getMaintenanceReportsGroupedByTechnician(OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK);

        verify(notificationReportDao).getNotificationReportGroupedByAccount(
                OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, NotificationType.Maintenance, AccountRole.Maintenance);
    }

    @Test
    public void getGlobalStatistics() {
        when(notificationReportDao.getGlobalInfo(any(), any(), any())).thenReturn(null);

        notificationReportService.getGlobalStatistics(OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, NotificationType.Maintenance);

        verify(notificationReportService).getGlobalStatistics(OFFSET_DATE_TIME_MOCK, OFFSET_DATE_TIME_MOCK, NotificationType.Maintenance);
    }
}