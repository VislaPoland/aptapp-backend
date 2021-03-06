package com.creatix.service.notification;

import com.creatix.controller.exception.AptValidationException;
import com.creatix.domain.dao.notifications.NotificationReportDao;
import com.creatix.domain.dto.notification.reporting.NotificationReportDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGlobalInfoDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGroupByAccountDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service for retrieving reporting under {@link com.creatix.domain.entity.store.notification.Notification} entities
 *
 * @see com.creatix.domain.entity.store.notification.BusinessProfileNotification
 * @see com.creatix.domain.entity.store.notification.CommentNotification
 * @see com.creatix.domain.entity.store.notification.CommunityBoardItemUpdatedSubscriberNotification
 * @see com.creatix.domain.entity.store.notification.DiscountCouponNotification
 * @see com.creatix.domain.entity.store.notification.EventInviteNotification
 * @see com.creatix.domain.entity.store.notification.MaintenanceNotification
 * @see com.creatix.domain.entity.store.notification.NeighborhoodNotification
 * @see com.creatix.domain.entity.store.notification.PersonalMessageNotification
 * @see com.creatix.domain.entity.store.notification.SecurityNotification
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@ParametersAreNonnullByDefault
public class NotificationReportService {

    private final NotificationReportDao notificationReportDao;

    /**
     * return notification report rows for give date period defined by from & till values for given notification type
     *
     * @param from left value of datetime range
     * @param till right value of datetime range
     * @param notificationType notification type consumer want to get reports
     * @param propertyId property under which we want to get statistics
     *
     * @return {@link com.creatix.domain.entity.store.notification.Notification} enhanced to cover the reporting needs
     */
    public List<NotificationReportDto> getReportsByRange(OffsetDateTime from, OffsetDateTime till,
                                                         NotificationType notificationType, Long propertyId) {
        return notificationReportDao.getNotificationReport(from, till, notificationType, propertyId);
    }

    /**
     * return {@link com.creatix.domain.entity.store.notification.MaintenanceNotification} report data
     * grouped by technician {@link com.creatix.domain.entity.store.account.Account}
     *
     * @param from left value of datetime range
     * @param till right value of datetime range
     * @param propertyId property under which we want to get statistics
     *
     * @return technician statistics for resolving and confirmation of {@link com.creatix.domain.entity.store.notification.MaintenanceNotification}
     */
    public List<NotificationReportGroupByAccountDto> getMaintenanceReportsGroupedByTechnician(OffsetDateTime from, OffsetDateTime till, Long propertyId) {
        return notificationReportDao.getNotificationReportGroupedByAccount(from, till, NotificationType.Maintenance, AccountRole.Maintenance, propertyId);
    }

    /**
     * return global information about the notification's resolution and confirmation for given type
     *
     * interface is prepared to consume any type of notification, but implemented only Maintenance yet.
     *
     * @param from left value of datetime range
     * @param till right value of datetime range
     * @param notificationType notification type consumer want to get reports
     * @param propertyId property under which we want to get statistics
     *
     * @return statistics for given notification type by given range
     */
    public NotificationReportGlobalInfoDto getGlobalStatistics(OffsetDateTime from, OffsetDateTime till,
                                                               NotificationType notificationType, Long propertyId) throws AptValidationException {
        if(!NotificationType.Maintenance.equals(notificationType)) {
            throw new AptValidationException(String.format("Unable to get global statistics. Unsupported notification type %s", notificationType.name()));
        }

        return notificationReportDao.getMaintenanceGlobalInfo(from, till, propertyId);
    }
}
