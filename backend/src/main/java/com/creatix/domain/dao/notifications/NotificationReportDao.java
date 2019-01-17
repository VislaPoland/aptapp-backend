package com.creatix.domain.dao.notifications;

import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportAccountDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGlobalInfoDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGroupByAccountDto;
import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationType;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.creatix.domain.entity.store.notification.Notification.*;

/**
 * Repository to fetch statistics for {@link Notification} table and it's journal table {@link com.creatix.domain.entity.store.notification.NotificationHistory}
 */
@Repository
public class NotificationReportDao {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String PARAM_TYPE = "type";
    private static final String PARAM_FROM = "from";
    private static final String PARAM_TO = "to";
    private static final String PARAM_PROPERTY_ID = "propertyId";
    private static final String PARAM_ROLE = "role";

    // cache sql query templates statically
    private static final String QUERY_MAINTENANCE_GLOBAL_INFO = getSqlQuery("maintenanceGlobalInfo.sql");
    private static final String QUERY_GROUPED_NOTIFICATION_REPORT_BY_ACCOUNT = getSqlQuery("groupedNotificationReportByAccount.sql");
    private static final String QUERY_NOTIFICATION_REPORT = getSqlQuery("notificationReport.sql");

    private static String getSqlQuery(String queryName) {
        String QUERY_MAINTENANCE_GLOBAL_INFO_TEMP;
        String path = Paths.get("notification", "sql", queryName).toString();

        try (InputStream inputStream = NotificationReportDao.class.getClassLoader().getResourceAsStream(path)) {
            assert inputStream != null;
            QUERY_MAINTENANCE_GLOBAL_INFO_TEMP = IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            QUERY_MAINTENANCE_GLOBAL_INFO_TEMP = null;
        }

        return QUERY_MAINTENANCE_GLOBAL_INFO_TEMP;
    }

    /**
     * retrieve global info for Maintenance notifications which are connected to maintenance_reservation table
     * {@link com.creatix.domain.entity.store.MaintenanceReservation}
     *
     * @param from       left value of datetime range
     * @param till       right value of datetime range
     * @param propertyId property for which we are collecting statistics data
     * @return results of statistic query
     */
    public NotificationReportGlobalInfoDto getMaintenanceGlobalInfo(OffsetDateTime from,
                                                                    OffsetDateTime till,
                                                                    Long propertyId) {

        Query query = entityManager.createNativeQuery(QUERY_MAINTENANCE_GLOBAL_INFO,
                MAINTENANCE_GLOBAL_INFO_MAPPING);
        fillQueryParameters(from, till, propertyId, NotificationType.Maintenance, query);

        return (NotificationReportGlobalInfoDto) query.getSingleResult();
    }

    /**
     * Return notification report based on notification table.
     * <p>
     * reports are calculated from joined notification_history table and status is set primary from it's last status value
     * or from notification's status column.
     * <p>
     * it would be nice to have all notification's history for all types stored in notification_history to be able to remove
     * complexity from query.
     *
     * @param from             left value of datetime range
     * @param till             right value of datetime range
     * @param notificationType filter result by type
     * @param propertyId       property for which we are collecting statistics data
     * @return results of statistic query
     */
    @SuppressWarnings("unchecked")
    public List<NotificationReportDto> getNotificationReport(OffsetDateTime from,
                                                             OffsetDateTime till,
                                                             NotificationType notificationType, Long propertyId) {
        Query query = entityManager.createNativeQuery(QUERY_NOTIFICATION_REPORT, NOTIFICATION_REPORT_MAPPING);
        fillQueryParameters(from, till, propertyId, notificationType, query);
        return (List<NotificationReportDto>) query.getResultList()
                .stream()
                .map(mappedEntiites -> mapReportFromArray((Object[]) mappedEntiites))
                .collect(Collectors.toList());
    }

    /**
     * Return notification report results grouped by account with given {@link AccountRole}
     * <p>
     * used mainly for {@link com.creatix.domain.entity.store.notification.MaintenanceNotification}.
     * <p>
     * in case we should do group by without or with more roles, this method should be extended or overloaded
     *
     * @param from             left value of datetime range
     * @param till             right value of datetime range
     * @param notificationType filter result by type
     * @param accountRole      accountRole for which to join results (and group by)
     * @param propertyId       property for which we are collecting statistics data
     * @return results of statistic query
     */
    @SuppressWarnings("unchecked")
    public List<NotificationReportGroupByAccountDto>
    getNotificationReportGroupedByAccount(OffsetDateTime from,
                                          OffsetDateTime till,
                                          NotificationType notificationType,
                                          AccountRole accountRole, Long propertyId) {
        Query query = entityManager.createNativeQuery(QUERY_GROUPED_NOTIFICATION_REPORT_BY_ACCOUNT,
                NOTIFICATION_REPORT_GROUPED_BY_ACCOUNT_MAPPING);
        fillQueryParameters(from, till, propertyId, notificationType, query);
        query.setParameter(PARAM_ROLE, accountRole.name());

        return (List<NotificationReportGroupByAccountDto>) query.getResultList()
                .stream()
                .map(mappedEntiites -> mapReportGroupedFromArray((Object[]) mappedEntiites))
                .collect(Collectors.toList());
    }

    /**
     * @param mappedEntities Object[] from mapping {@link Notification#NOTIFICATION_REPORT_GROUPED_BY_ACCOUNT_MAPPING}
     * @return mapped {@link NotificationReportGroupByAccountDto}
     */
    private static NotificationReportGroupByAccountDto mapReportGroupedFromArray(Object[] mappedEntities) {
        NotificationReportGroupByAccountDto result = (NotificationReportGroupByAccountDto) mappedEntities[0];
        result.setAccount((NotificationReportAccountDto) mappedEntities[1]);
        BasicApartmentDto apartment = (BasicApartmentDto) mappedEntities[2];

        if (apartment != null) {
            result.getAccount().setApartment(apartment);
        }

        return result;
    }

    /**
     * @param mappedEntities Object[] from mapping {@link Notification#NOTIFICATION_REPORT_MAPPING}
     * @return mapped {@link NotificationReportDto}
     */
    private static NotificationReportDto mapReportFromArray(Object[] mappedEntities) {
        NotificationReportDto result = (NotificationReportDto) mappedEntities[0];
        BasicApartmentDto apartment = (BasicApartmentDto) mappedEntities[1];

        if (apartment.getId() != null) {
            result.setTargetApartment(apartment);
        }

        NotificationReportAccountDto account = (NotificationReportAccountDto) mappedEntities[2];
        result.setCreatedBy(account);
        apartment = (BasicApartmentDto) mappedEntities[3];

        if (apartment.getId() != null) {
            account.setApartment(apartment);
        }

        account = (NotificationReportAccountDto) mappedEntities[4];
        if (account.getId() != null) {
            result.setRespondedBy(account);
        }

        account = (NotificationReportAccountDto) mappedEntities[5];
        if (account.getId() != null) {
            result.setResolvedBy(account);
        }

        return result;
    }

    /**
     * Common helper method to map the same query parameters
     *
     * @param from       parameter
     * @param till       parameter
     * @param propertyId parameter
     * @param type       parameter
     * @param query      query which will use parameter in it's fetching
     */
    private void fillQueryParameters(OffsetDateTime from, OffsetDateTime till, Long propertyId, NotificationType type, final Query query) {
        query.setParameter(PARAM_FROM, from);
        query.setParameter(PARAM_TO, till);
        query.setParameter(PARAM_PROPERTY_ID, propertyId);
        query.setParameter(PARAM_TYPE, type.name());
    }
}
