package com.creatix.service;

import com.creatix.configuration.MailProperties;
import com.creatix.domain.dao.*;
import com.creatix.domain.entity.*;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.creatix.security.AuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private MaintenanceNotificationDao maintenanceNotificationDao;
    @Autowired
    private NeighborhoodNotificationDao neighborhoodNotificationDao;
    @Autowired
    private SecurityNotificationDao securityNotificationDao;
    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private MailSender mailSender;

    public Map<Integer, List<Notification>> getRelevantInDateRangeGroupedByDayNumber(Date fromDate, Date tillDate) {
        return notificationDao.findAllInDateRange(fromDate, tillDate).stream()
                .filter(n -> relevantNotificationsFilter(n, authorizationManager.getCurrentAccount()))
                .collect(Collectors.groupingBy(n -> extractDayNumber(n.getDate())));
    }

    public List<Notification> getRelevantInDateRange(Date fromDate, Date tillDate) {
        return notificationDao.findAllInDateRange(fromDate, tillDate).stream()
                .filter(n -> relevantNotificationsFilter(n, authorizationManager.getCurrentAccount()))
                .collect(Collectors.toList());
    }

    public List<MaintenanceNotification> getMaintenanceNotificationsInDateRange(Date fromDate, Date tillDate) {
        return notificationDao.findAllMaintenanceInDateRange(fromDate, tillDate);
    }

    public SecurityNotification getSecurityNotification(Long notificationId) {
        SecurityNotification n = securityNotificationDao.findById(notificationId);
        if (n == null) {
            throw new EntityNotFoundException(String.format("Account id=%d not found", notificationId));
        }

        return n;
    }

    public MaintenanceNotification getMaintenanceNotification(Long notificationId) {
        MaintenanceNotification n = maintenanceNotificationDao.findById(notificationId);
        if (n == null) {
            throw new EntityNotFoundException(String.format("Account id=%d not found", notificationId));
        }

        return n;
    }

    public NeighborhoodNotification getNeighborhoodNotification(Long notificationId) {
        NeighborhoodNotification n = neighborhoodNotificationDao.findById(notificationId);
        if (n == null) {
            throw new EntityNotFoundException(String.format("Account id=%d not found", notificationId));
        }

        return n;
    }

    public SecurityNotification saveSecurityNotification(SecurityNotification n) {
        n.setType(NotificationType.Security);
        n.setAuthor(authorizationManager.getCurrentAccount());
        n.setStatus(NotificationStatus.Pending);
        saveNotification(n, securityNotificationDao);
        return n;
    }

    public MaintenanceNotification saveMaintenanceNotification(String targetUnitNumber, MaintenanceNotification n) {
        n.setType(NotificationType.Maintenance);
        n.setAuthor(authorizationManager.getCurrentAccount());
        n.setStatus(NotificationStatus.Pending);
        Apartment apartment = apartmentDao.findByUnitNumberWithinProperty(targetUnitNumber, authorizationManager.getCurrentProperty());
        if (apartment == null) {
            throw new EntityNotFoundException(String.format("Apartment with unit number=%s not found", targetUnitNumber));
        }
        n.setTargetApartment(apartment);
        saveNotification(n, maintenanceNotificationDao);
        return n;
    }

    public NeighborhoodNotification saveNeighborhoodNotification(String targetUnitNumber, NeighborhoodNotification n) {
        n.setType(NotificationType.Neighborhood);
        n.setAuthor(authorizationManager.getCurrentAccount());
        n.setStatus(NotificationStatus.Pending);
        Apartment apartment = apartmentDao.findByUnitNumberWithinProperty(targetUnitNumber, authorizationManager.getCurrentProperty());
        if (apartment == null) {
            throw new EntityNotFoundException(String.format("Apartment with unit number=%s not found", targetUnitNumber));
        }
        n.setTargetApartment(apartment);
        saveNotification(n, neighborhoodNotificationDao);
        return n;
    }

    private <T extends Notification> void saveNotification(T n, AbstractNotificationDao<T> dao) {
        if (n.getCreatedAt() == null) {
            n.setCreatedAt(new Date());
            n.setUpdatedAt(new Date());
        } else {
            n.setUpdatedAt(new Date());
        }
        dao.persist(n);
    }

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
                    //noinspection ConstantConditions
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

    private void sendMail(String to, String subject, String body) {

        if (StringUtils.isEmpty(mailProperties.getFrom())) {
            throw new IllegalStateException("'From' address not defined in configuration");
        }

        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailProperties.getFrom());
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailSender.send(mailMessage);
    }
}
