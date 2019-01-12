package com.creatix.domain.dao.notifications;

import com.creatix.domain.dto.notification.reporting.NotificationReportDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGlobalInfoDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGroupByAccountDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class NotificationReportDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * retrieve global info for Maintenance notifications which are connected to maintenance_reservation table
     * {@link com.creatix.domain.entity.store.MaintenanceReservation}
     *
     * @param from left value of datetime range
     * @param till right value of datetime range
     * @param propertyId property for which we are collecting statistics data
     *
     * @return results of statistic query
     */
    public NotificationReportGlobalInfoDto getMaintenanceGlobalInfo(OffsetDateTime from,
                                                                    OffsetDateTime till,
                                                                    Long propertyId) {
        return null;
    }

    /**
     *
     * @param from left value of datetime range
     * @param till right value of datetime range
     * @param notificationType filter result by type
     * @param propertyId property for which we are collecting statistics data
     *
     * @return results of statistic query
     */
    public List<NotificationReportDto> getNotificationReport(OffsetDateTime from,
                                                             OffsetDateTime till,
                                                             NotificationType notificationType, Long propertyId) {
        return null;
    }

    /**
     *
     * @param from left value of datetime range
     * @param till right value of datetime range
     * @param notificationType filter result by type
     * @param accountRole accountRole for which to join results (and group by)
     * @param propertyId property for which we are collecting statistics data
     *
     * @return results of statistic query
     */
    public List<NotificationReportGroupByAccountDto>
    getNotificationReportGroupedByAccount(OffsetDateTime from,
                                          OffsetDateTime till,
                                          NotificationType notificationType,
                                          AccountRole accountRole, Long propertyId) {
        return null;
    }
}
