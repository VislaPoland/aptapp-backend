package com.creatix.domain.dao;

import com.creatix.domain.entity.Account;
import com.creatix.domain.entity.MaintenanceNotification;
import com.creatix.domain.entity.NeighborhoodNotification;
import com.creatix.domain.entity.Notification;
import com.creatix.domain.enums.NotificationType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Transactional
public class NotificationDao extends AbstractNotificationDao<Notification> {
    public Map<Integer, List<Notification>> findAllInDateRangeGroupedByDayFilteredByAccount(Date fromDate, Date tillDate, Account account) {
        return em.createQuery("SELECT n FROM Notification n WHERE n.date BETWEEN :fromDate AND :tillDate", Notification.class)
                .setParameter("fromDate", fromDate)
                .setParameter("tillDate", tillDate)
                .getResultList()
                .stream()
                .filter(n -> relevantNotificationsFilter(n, account))
                .collect(Collectors.groupingBy(notification -> extractDayNumber(notification.getDate())));
    }

    @SuppressWarnings("ConstantConditions")
    private boolean relevantNotificationsFilter(Notification n, Account a) {
        switch (a.getRole()) {
            case Maintenance:
                return n.getType().equals(NotificationType.Maintenance);
            case Security:
                return n.getType().equals(NotificationType.Security);
            case Tenant:
                boolean r = n.getAuthor().equals(a);
                if (n.getType().equals(NotificationType.Maintenance))
                    r = r || ((MaintenanceNotification) n).getTargetApartment().getTenant().equals(a);
                if (n.getType().equals(NotificationType.Neighborhood))
                    r = r || ((NeighborhoodNotification) n).getTargetApartment().getTenant().equals(a);
                return r;
            default:
                return false;
        }
    }

    private int extractDayNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
