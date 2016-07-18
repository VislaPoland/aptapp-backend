package com.creatix.service;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.enums.NotificationRequestType;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.NeighborNotification;
import com.creatix.security.AuthorizationManager;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

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
    @Autowired
    private SmsMessageSender smsMessageSender;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }


    public PageableDataResponse<List<Notification>> filterNotifications(
            @NotNull NotificationRequestType requestType,
            @Nullable NotificationStatus notificationStatus,
            @Nullable NotificationType notificationType,
            @Nullable Long startId,
            int pageSize) {
        final Account account = authorizationManager.getCurrentAccount();

        final List<Notification> notifications = notificationDao.findPageByNotificationStatusAndNotificationTypeAndRequestTypeAndAccount(
                requestType,
                notificationStatus,
                notificationType,
                startId,
                account,
                pageSize + 1);

        final Long nextId = notifications.size() > pageSize ? notifications.get(pageSize).getId() : null;

        return new PageableDataResponse<>(notifications, (long) pageSize, nextId);
    }



    public PageableDataResponse<List<Notification>> filterNotifications(NotificationRequestType type, long pageNumber, long pageSize) {
        final Account account = authorizationManager.getCurrentAccount();

        long totalItems = notificationDao.countByType(type, account);
        long totalPages = (long) Math.ceil(totalItems / (double) pageSize);

        List<Notification> data = notificationDao.findPageByType(type, account, pageNumber, pageSize);
        return new PageableDataResponse<>(data, pageSize, totalItems, totalPages, pageNumber);
    }

    public List<MaintenanceNotification> getAllMaintenanceNotificationsInDateRange(@NotNull Date beginDate, @NotNull Date endDate) {
        Objects.requireNonNull(beginDate);
        Objects.requireNonNull(endDate);
        return maintenanceNotificationDao.findAllInDateRange(beginDate, endDate);
    }

    public PageableDataResponse<List<MaintenanceNotification>> filterMaintenanceNotifications(
            @NotNull NotificationRequestType type, @Nullable NotificationStatus status, long pageNumber, long pageSize) {
        final Account account = authorizationManager.getCurrentAccount();

        long totalItems = maintenanceNotificationDao.countByStatusAndType(status, type, account);
        long totalPages = (long) Math.ceil(totalItems / (double) pageSize);

        List<MaintenanceNotification> data = maintenanceNotificationDao.findPageByStatusAndType(status, type, account, pageNumber, pageSize);
        return new PageableDataResponse<>(data, pageSize, totalItems, totalPages, pageNumber);
    }

    public PageableDataResponse<List<NeighborhoodNotification>> filterNeighborhoodNotifications(
            NotificationRequestType type, NotificationStatus status, long pageNumber, long pageSize) {
        final Account account = authorizationManager.getCurrentAccount();

        long totalItems = neighborhoodNotificationDao.countByStatusAndType(status, type, account);
        long totalPages = (long) Math.ceil(totalItems / (double) pageSize);

        List<NeighborhoodNotification> data = neighborhoodNotificationDao.findPageByStatusAndType(status, type, account, pageNumber, pageSize);
        return new PageableDataResponse<>(data, pageSize, totalItems, totalPages, pageNumber);
    }

    public PageableDataResponse<List<SecurityNotification>> filterSecurityNotifications(
            NotificationRequestType type, NotificationStatus status, long pageNumber, long pageSize) {
        final Account account = authorizationManager.getCurrentAccount();

        long totalItems = securityNotificationDao.countByStatusAndType(status, type, account);
        long totalPages = (long) Math.ceil(totalItems / (double) pageSize);

        List<SecurityNotification> data = securityNotificationDao.findPageByStatusAndType(status, type, account, pageNumber, pageSize);
        return new PageableDataResponse<>(data, pageSize, totalItems, totalPages, pageNumber);
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

    public NeighborhoodNotification saveNeighborhoodNotification(@NotNull String targetUnitNumber, @NotNull NeighborhoodNotification notification) throws MessageDeliveryException, TemplateException, IOException {
        Objects.requireNonNull(targetUnitNumber);
        Objects.requireNonNull(notification);

        notification.setType(NotificationType.Neighborhood);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setProperty(authorizationManager.getCurrentProperty());
        notification.setStatus(NotificationStatus.Pending);
        final Apartment targetApartment = getApartmentByUnitNumber(targetUnitNumber);
        notification.setTargetApartment(targetApartment);
        neighborhoodNotificationDao.persist(notification);

        final Property property = targetApartment.getProperty();
        final Tenant tenant = targetApartment.getTenant();
        if ( (tenant != null) && (property.getEnableSms() == Boolean.TRUE) && (tenant.getEnableSms() == Boolean.TRUE) ) {
            smsMessageSender.send(new NeighborNotification(tenant));
        }

        return notification;
    }

    private Apartment getApartmentByUnitNumber(String targetUnitNumber) {
        final Apartment apartment = apartmentDao.findByUnitNumberWithinProperty(targetUnitNumber, authorizationManager.getCurrentProperty());
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment with unit number=%s not found", targetUnitNumber));
        }
        return apartment;
    }

    public Notification storeNotificationPhotos(MultipartFile[] files, long notificationId) throws IOException {

        final Notification notification = notificationDao.findById(notificationId);
        if ( notification == null ) {
            throw new EntityNotFoundException(String.format("Notification id=%d not found", notificationId));
        }

        for ( MultipartFile file : files ) {

            // move uploaded file to file repository
            final String fileName = String.format("%d-%d-%s", notification.getId(), notification.getPhotos().size(), file.getOriginalFilename());
            final Path photoFilePath = Paths.get(uploadProperties.getRepositoryPath(), fileName);
            Files.createDirectories(photoFilePath.getParent());
            file.transferTo(photoFilePath.toFile());

            final NotificationPhoto photo = new NotificationPhoto();
            photo.setNotification(notification);
            photo.setFileName(fileName);
            photo.setFilePath(photoFilePath.toString());
            notificationPhotoDao.persist(photo);

            notification.getPhotos().add(photo);
        }

        return notification;
    }

    public NotificationPhoto getNotificationPhoto(Long notificationId, String fileName) {

        final NotificationPhoto photo = notificationPhotoDao.findByNotificationIdAndFileName(notificationId, fileName);
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%s not found", fileName));
        }

        return photo;
    }
}
