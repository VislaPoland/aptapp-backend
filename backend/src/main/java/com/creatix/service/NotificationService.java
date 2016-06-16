package com.creatix.service;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.dao.MaintenanceNotificationDao;
import com.creatix.domain.dao.NeighborhoodNotificationDao;
import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.entity.MaintenanceNotification;
import com.creatix.domain.entity.NeighborhoodNotification;
import com.creatix.domain.entity.Notification;
import com.creatix.domain.enums.NotificationType;
import com.creatix.security.AuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private AccountDao accountDao;
    @Autowired
    private AuthorizationManager authorizationManager;

    public Map<Integer, List<Notification>> getAllInDateRangeGroupedByDay(Date from, Date till) {
        return notificationDao.findAllInDateRangeGroupedByDayFilteredByAccount(from, till, accountDao.findById(authorizationManager.getCurrentAccount().getId()));
    }

    public Notification getSecurityNotification(Long notificationId) {
        Notification n = notificationDao.findById(notificationId);
        if (n == null)
            throw new EntityNotFoundException(String.format("Account id=%d not found", notificationId));

        return n;
    }

    public MaintenanceNotification getMaintenanceNotification(Long notificationId) {
        MaintenanceNotification n = maintenanceNotificationDao.findById(notificationId);
        if (n == null)
            throw new EntityNotFoundException(String.format("Account id=%d not found", notificationId));

        return n;
    }

    public NeighborhoodNotification getNeighborhoodNotification(Long notificationId) {
        NeighborhoodNotification n = neighborhoodNotificationDao.findById(notificationId);
        if (n == null)
            throw new EntityNotFoundException(String.format("Account id=%d not found", notificationId));

        return n;
    }

    public Notification saveSecurityNotification(Notification n) {
        n.setType(NotificationType.Security);
        n.setAuthor(accountDao.findById(authorizationManager.getCurrentAccount().getId()));
        notificationDao.persist(n);
        return n;
    }

    public MaintenanceNotification saveMaintenanceNotification(MaintenanceNotification n) {
        n.setType(NotificationType.Maintenance);
        n.setAuthor(accountDao.findById(authorizationManager.getCurrentAccount().getId()));
        notificationDao.persist(n);
        return n;
    }

    public NeighborhoodNotification saveNeighborhoodNotification(NeighborhoodNotification n) {
        n.setType(NotificationType.Neighborhood);
        n.setAuthor(accountDao.findById(authorizationManager.getCurrentAccount().getId()));
        notificationDao.persist(n);
        return n;
    }
}
