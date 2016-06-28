package com.creatix.service;

import com.creatix.configuration.FileUploadProperties;
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
    private MailProperties mailProperties;
    @Autowired
    private FileUploadProperties uploadProperties;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private NotificationPhotoDao notificationPhotoDao;

    public List<Notification> getRelevantInDateRange(@NotNull Date beginDate, @NotNull Date endDate) {
        Objects.requireNonNull(beginDate);
        Objects.requireNonNull(endDate);
        return notificationDao.findAllInDateRange(beginDate, endDate).stream()
                .filter(n -> relevantNotificationsFilter(n, authorizationManager.getCurrentAccount()))
                .collect(Collectors.toList());
    }

    public List<MaintenanceNotification> getMaintenanceNotificationsInDateRange(@NotNull Date beginDate, @NotNull Date endDate) {
        Objects.requireNonNull(beginDate);
        Objects.requireNonNull(endDate);
        return maintenanceNotificationDao.findAllInDateRange(beginDate, endDate);
    }

    public SecurityNotification getSecurityNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId);
        SecurityNotification n = securityNotificationDao.findById(notificationId);
        if ( n == null ) {
            throw new EntityNotFoundException(String.format("Notification id=%d not found", notificationId));
        }

        return n;
    }

    public MaintenanceNotification getMaintenanceNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId);
        MaintenanceNotification n = maintenanceNotificationDao.findById(notificationId);
        if ( n == null ) {
            throw new EntityNotFoundException(String.format("Notification id=%d not found", notificationId));
        }

        return n;
    }

    public NeighborhoodNotification getNeighborhoodNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId);
        NeighborhoodNotification n = neighborhoodNotificationDao.findById(notificationId);
        if ( n == null ) {
            throw new EntityNotFoundException(String.format("Notification id=%d not found", notificationId));
        }

        return n;
    }

    public SecurityNotification saveSecurityNotification(@NotNull SecurityNotification notification) {
        Objects.requireNonNull(notification);
        notification.setType(NotificationType.Security);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setStatus(NotificationStatus.Pending);
        securityNotificationDao.persist(notification);
        return notification;
    }

    public MaintenanceNotification saveMaintenanceNotification(@NotNull String targetUnitNumber, @NotNull MaintenanceNotification notification) {
        Objects.requireNonNull(targetUnitNumber);
        Objects.requireNonNull(notification);

        notification.setType(NotificationType.Maintenance);
        notification.setAuthor(authorizationManager.getCurrentAccount());
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
        notification.setStatus(NotificationStatus.Pending);
        notification.setTargetApartment(getApartmentByUnitNumber(targetUnitNumber));
        neighborhoodNotificationDao.persist(notification);
        return notification;
    }

    private Apartment getApartmentByUnitNumber(String targetUnitNumber) {
        final Apartment apartment = apartmentDao.findByUnitNumberWithinProperty(targetUnitNumber, authorizationManager.getCurrentProperty());
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment with unit number=%s not found", targetUnitNumber));
        }
        return apartment;
    }

    private boolean relevantNotificationsFilter(Notification n, Account a) {
        switch ( a.getRole() ) {
            case Maintenance:
                return n.getType().equals(NotificationType.Maintenance);
            case Security:
                return n.getType().equals(NotificationType.Security);
            case Tenant:
                boolean r = n.getAuthor().equals(a);
                if ( n.getType().equals(NotificationType.Maintenance) ) {
                    r = r || ((MaintenanceNotification) n).getTargetApartment().getTenant().equals(a);
                }
                if ( n.getType().equals(NotificationType.Neighborhood) ) {
                    //noinspection ConstantConditions
                    r = r || ((NeighborhoodNotification) n).getTargetApartment().getTenant().equals(a);
                }
                return r;
            default:
                return false;
        }
    }

    private void sendMail(String to, String subject, String body) {

        if ( StringUtils.isEmpty(mailProperties.getFrom()) ) {
            throw new IllegalStateException("'From' address not defined in configuration");
        }

        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailProperties.getFrom());
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailSender.send(mailMessage);
    }

    public Notification storeNotificationPhotos(MultipartFile[] files, long notificationId) throws IOException {

        final Notification notification = notificationDao.findById(notificationId);
        if ( notification == null ) {
            throw new EntityNotFoundException(String.format("Notification id=%d not found", notificationId));
        }

        for ( MultipartFile file : files ) {

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
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%d not found", notificationPhotoId));
        }

        return photo;
    }
}
