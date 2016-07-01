package com.creatix.service;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.notification.NotificationRequestType;
import com.creatix.domain.entity.*;
import com.creatix.domain.entity.account.Account;
import com.creatix.domain.entity.account.Employee;
import com.creatix.domain.entity.account.PropertyManager;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.creatix.security.AuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    private FileUploadProperties uploadProperties;
    @Autowired
    private NotificationPhotoDao notificationPhotoDao;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if (item == null) {
            throw ex;
        }
        return item;
    }

    //TODO make filtering logic more effective
    public PageableDataResponse<List<Notification>> getRelevantNotifications(Long pageNumber, Long pageSize, NotificationRequestType type) {
        List<Notification> notifications = notificationDao.findAll().stream()
                .filter(n -> relevantNotificationsFilter(n, authorizationManager.getCurrentAccount(), type))
                .collect(Collectors.toList());
        long totalItems = notifications.size();
        long totalPages = totalItems / pageSize;

        return new PageableDataResponse<>(notifications.stream()
                .limit(pageNumber * pageSize + pageSize)
                .skip(pageNumber * pageSize)
                .collect(Collectors.toList()), pageSize, totalItems, totalPages, pageNumber);
    }

    public List<MaintenanceNotification> getMaintenanceNotificationsInDateRange(@NotNull Date beginDate, @NotNull Date endDate) {
        Objects.requireNonNull(beginDate);
        Objects.requireNonNull(endDate);
        return maintenanceNotificationDao.findAllInDateRange(beginDate, endDate);
    }

    public SecurityNotification getSecurityNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId);

        return getOrElseThrow(notificationId, securityNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public MaintenanceNotification getMaintenanceNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId);

        return getOrElseThrow(notificationId, maintenanceNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public NeighborhoodNotification getNeighborhoodNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId);

        return getOrElseThrow(notificationId, neighborhoodNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public SecurityNotification saveSecurityNotification(@NotNull SecurityNotification notification) {
        Objects.requireNonNull(notification);
        notification.setType(NotificationType.Security);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setProperty(authorizationManager.getCurrentProperty());
        notification.setStatus(NotificationStatus.Pending);
        securityNotificationDao.persist(notification);
        return notification;
    }

    public MaintenanceNotification saveMaintenanceNotification(@NotNull String targetUnitNumber, @NotNull MaintenanceNotification notification) {
        Objects.requireNonNull(targetUnitNumber);
        Objects.requireNonNull(notification);

        notification.setType(NotificationType.Maintenance);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setProperty(authorizationManager.getCurrentProperty());
        notification.setStatus(NotificationStatus.Pending);
        notification.setTargetApartment(getApartmentByUnitNumber(targetUnitNumber));
        maintenanceNotificationDao.persist(notification);
        return notification;
    }

    public NeighborhoodNotification saveNeighborhoodNotification(@NotNull String targetUnitNumber, @NotNull NeighborhoodNotification notification) {
        Objects.requireNonNull(targetUnitNumber);
        Objects.requireNonNull(notification);

        notification.setType(NotificationType.Neighborhood);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setProperty(authorizationManager.getCurrentProperty());
        notification.setStatus(NotificationStatus.Pending);
        notification.setTargetApartment(getApartmentByUnitNumber(targetUnitNumber));
        neighborhoodNotificationDao.persist(notification);
        return notification;
    }

    private Apartment getApartmentByUnitNumber(String targetUnitNumber) {
        final Apartment apartment = apartmentDao.findByUnitNumberWithinProperty(targetUnitNumber, authorizationManager.getCurrentProperty());
        if (apartment == null) {
            throw new EntityNotFoundException(String.format("Apartment with unit number=%s not found", targetUnitNumber));
        }
        return apartment;
    }

    private boolean relevantNotificationsFilter(Notification n, Account a, NotificationRequestType type) {
        switch (type) {
            case Send:
                return n.getAuthor().equals(a);
            case Received:
                switch (a.getRole()) {
                    case PropertyManager:
                        return ((PropertyManager) a).getManagedProperty().equals(n.getProperty());
                    case AssistantPropertyManager:
                        return authorizationManager.getCurrentProperty().equals(n.getProperty());
                    case Maintenance:
                        return authorizationManager.getCurrentProperty().equals(n.getProperty()) && n.getType().equals(NotificationType.Maintenance);
                    case Security:
                        return authorizationManager.getCurrentProperty().equals(n.getProperty()) && n.getType().equals(NotificationType.Security);
                    case Tenant:
                        if (n.getType().equals(NotificationType.Maintenance)) {
                            return a.equals(((MaintenanceNotification) n).getTargetApartment().getTenant());
                        }
                        if (n.getType().equals(NotificationType.Neighborhood)) {
                            return a.equals(((NeighborhoodNotification) n).getTargetApartment().getTenant());
                        }
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    public Notification storeNotificationPhotos(MultipartFile[] files, long notificationId) throws IOException {

        final Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new EntityNotFoundException(String.format("Notification id=%d not found", notificationId));
        }

        for (MultipartFile file : files) {

            // move uploaded file to file repository
            final Path photoFilePath = Paths.get(uploadProperties.getRepositoryPath(), String.format("%d-%d-%s", notification.getId(), notification.getPhotos().size(), file.getOriginalFilename()));
            file.transferTo(photoFilePath.toFile());

            final NotificationPhoto photo = new NotificationPhoto();
            photo.setNotification(notification);
            photo.setFileName(file.getOriginalFilename());
            photo.setFilePath(photoFilePath.toString());
            notificationPhotoDao.persist(photo);

            notification.getPhotos().add(photo);
        }

        return notification;
    }

    public NotificationPhoto getNotificationPhoto(long notificationPhotoId) {

        final NotificationPhoto photo = notificationPhotoDao.findById(notificationPhotoId);
        if (photo == null) {
            throw new EntityNotFoundException(String.format("Photo id=%d not found", notificationPhotoId));
        }

        return photo;
    }
}
