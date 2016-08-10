package com.creatix.service;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationResponseRequest;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationResponseRequest;
import com.creatix.domain.dto.notification.security.SecurityNotificationResponseRequest;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.MaintenanceEmployee;
import com.creatix.domain.entity.store.account.SecurityEmployee;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.*;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.PushNotificationSender;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.push.*;
import com.creatix.security.AuthorizationManager;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Optional;
import java.util.stream.Stream;

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
    @Autowired
    private SmsMessageSender smsMessageSender;
    @Autowired
    private PushNotificationSender pushNotificationSender;
    @Autowired
    private SecurityEmployeeDao securityEmployeeDao;
    @Autowired
    private MaintenanceEmployeeDao maintenanceEmployeeDao;
    @Autowired
    private MaintenanceReservationService maintenanceReservationService;

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
        Objects.requireNonNull(requestType, "Request type is null");

        final Account account = authorizationManager.getCurrentAccount();

        List<Notification> notifications = notificationDao.findPageByNotificationStatusAndNotificationTypeAndRequestTypeAndAccount(
                requestType,
                notificationStatus,
                notificationType,
                startId,
                account,
                pageSize + 1);


        final Long nextId;
        if ( notifications.size() > pageSize ) {
            nextId = notifications.get(pageSize).getId();
            notifications = notifications.subList(0, pageSize);
        }
        else {
            nextId = null;
        }

        return new PageableDataResponse<>(notifications, (long) pageSize, nextId);
    }

    public List<MaintenanceNotification> getAllMaintenanceNotificationsInDateRange(@NotNull Date beginDate, @NotNull Date endDate) {
        Objects.requireNonNull(beginDate, "Begin date is null");
        Objects.requireNonNull(endDate, "End date is null");
        return maintenanceNotificationDao.findAllInDateRange(beginDate, endDate);
    }

    public SecurityNotification getSecurityNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId, "Notification id is null");

        return getOrElseThrow(notificationId, securityNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public MaintenanceNotification getMaintenanceNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId, "Notification id is null");

        return getOrElseThrow(notificationId, maintenanceNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public NeighborhoodNotification getNeighborhoodNotification(@NotNull Long notificationId) {
        Objects.requireNonNull(notificationId, "Notification id is null");

        return getOrElseThrow(notificationId, neighborhoodNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public SecurityNotification saveSecurityNotification(@NotNull SecurityNotification notification) throws IOException, TemplateException {
        Objects.requireNonNull(notification, "Notification is null");
        notification.setType(NotificationType.Security);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setProperty(authorizationManager.getCurrentProperty());
        notification.setStatus(NotificationStatus.Pending);
        securityNotificationDao.persist(notification);

        for ( SecurityEmployee secEmp : securityEmployeeDao.findByProperty(notification.getProperty()) ) {
            pushNotificationSender.sendNotification(new SecurityNotificationTemplate(notification), secEmp);
        }

        return notification;
    }

    public MaintenanceNotification saveMaintenanceNotification(@NotNull String targetUnitNumber, @NotNull MaintenanceNotification notification, @NotNull Long slotUnitId) throws IOException, TemplateException {
        Objects.requireNonNull(targetUnitNumber, "Target unit number is null");
        Objects.requireNonNull(notification, "Notification is null");
        Objects.requireNonNull(slotUnitId, "slot unit id is null");

        notification.setType(NotificationType.Maintenance);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setProperty(authorizationManager.getCurrentProperty());
        notification.setStatus(NotificationStatus.Pending);
        notification.setTargetApartment(getApartmentByUnitNumber(targetUnitNumber));
        maintenanceNotificationDao.persist(notification);

        maintenanceReservationService.createMaintenanceReservation(notification, slotUnitId);

        for ( MaintenanceEmployee employee : maintenanceEmployeeDao.findByProperty(notification.getProperty()) ) {
            pushNotificationSender.sendNotification(new MaintenanceNotificationTemplate(notification), employee);
        }

        return notification;
    }

    public NeighborhoodNotification saveNeighborhoodNotification(@NotNull String targetUnitNumber, @NotNull NeighborhoodNotification notification) throws MessageDeliveryException, TemplateException, IOException {
        Objects.requireNonNull(targetUnitNumber, "Target unit number is null");
        Objects.requireNonNull(notification, "Notification is null");

        final Apartment targetApartment = getApartmentByUnitNumber(targetUnitNumber);
        final Property property = targetApartment.getProperty();
        authorizationManager.checkRead(property);

        notification.setType(NotificationType.Neighborhood);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setProperty(property);
        notification.setStatus(NotificationStatus.Pending);
        notification.setTargetApartment(targetApartment);
        neighborhoodNotificationDao.persist(notification);

        final Tenant tenant = targetApartment.getTenant();
        if ( tenant != null ) {
            if ( (property.getEnableSms() == Boolean.TRUE) && (tenant.getEnableSms() == Boolean.TRUE) && (StringUtils.isNotBlank(tenant.getPrimaryPhone())) ) {
                smsMessageSender.send(new com.creatix.message.template.sms.NeighborNotificationTemplate(tenant));
            }
            pushNotificationSender.sendNotification(new NeighborNotificationTemplate(notification), tenant);
        }

        return notification;
    }

    public NeighborhoodNotification respondToNeighborhoodNotification(long notificationId, @NotNull NeighborhoodNotificationResponseRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(request, "Notification response request is null");

        final NeighborhoodNotification notification = getOrElseThrow(notificationId, neighborhoodNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));

        final Tenant tenant = notification.getTargetApartment().getTenant();
        if ( tenant != null ) {
            if ( authorizationManager.isSelf(tenant) ) {
                notification.setResponse(request.getResponse());
                neighborhoodNotificationDao.persist(notification);

                if ( request.getResponse() == NeighborhoodNotificationResponse.Resolved ) {
                    pushNotificationSender.sendNotification(new NeighborNotificationResolvedTemplate(notification), tenant);
                }
                else if ( request.getResponse() == NeighborhoodNotificationResponse.SorryNotMe ) {
                    pushNotificationSender.sendNotification(new NeighborNotificationNotMeTemplate(notification), tenant);
                }

                return notification;
            }
        }
        throw new SecurityException("You are only eligible to respond to notifications targeted at your apartment");
    }

    public SecurityNotification respondToSecurityNotification(long notificationId, @NotNull SecurityNotificationResponseRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(request, "Notification response request is null");

        final SecurityNotification notification = getOrElseThrow(notificationId, securityNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));

        if ( notification.getProperty().equals(authorizationManager.getCurrentProperty()) ) {
            notification.setResponse(request.getResponse());
            notification.setStatus(NotificationStatus.Resolved);
            securityNotificationDao.persist(notification);

            if ( request.getResponse() == SecurityNotificationResponseType.NoIssueFound ) {
                final Account account = notification.getAuthor();

                if ( account.getRole() == AccountRole.Tenant ) {
                    pushNotificationSender.sendNotification(new SecurityNotificationNeighborNoIssueTemplate(notification), account);
                }
                else if ( account.getRole() == AccountRole.PropertyManager || account.getRole() == AccountRole.AssistantPropertyManager ) {
                    pushNotificationSender.sendNotification(new SecurityNotificationManagerNoIssueTemplate(notification), account);
                }
            }
            else if ( request.getResponse() == SecurityNotificationResponseType.Resolved ) {
                final Account account = notification.getAuthor();

                if ( account.getRole() == AccountRole.Tenant ) {
                    pushNotificationSender.sendNotification(new SecurityNotificationNeighborResolvedTemplate(notification), account);
                }
                else if ( account.getRole() == AccountRole.PropertyManager || account.getRole() == AccountRole.AssistantPropertyManager ) {
                    pushNotificationSender.sendNotification(new SecurityNotificationManagerResolvedTemplate(notification), account);
                }
            }

            return notification;
        }

        throw new SecurityException("You are not eligible to respond to security notifications from another property");
    }

    public MaintenanceNotification respondToMaintenanceNotification(@NotNull Long notificationId, @NotNull MaintenanceNotificationResponseRequest response) throws IOException, TemplateException {
        Objects.requireNonNull(notificationId, "Notification id is null");
        Objects.requireNonNull(response, "Notification reseponse dto is null");

        final MaintenanceNotification notification = getMaintenanceNotification(notificationId);
        final Stream<MaintenanceReservation> reservationStream = notification.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.Pending);
        final long pendingCount = reservationStream.count();
        if ( pendingCount == 0 ) {
            throw new IllegalArgumentException("No pending reservations found for notification");
        }
        if ( pendingCount > 1 ) {
            throw new IllegalStateException("Multiple pending reservations found for notification");
        }

        final Optional<MaintenanceReservation> optReservation = reservationStream.findAny();
        if ( !(optReservation.isPresent()) ) {
            throw new IllegalStateException("Application error");
        }


        final MaintenanceReservation reservation = optReservation.get();

        if ( response.getResponse() == MaintenanceNotificationResponseRequest.ResponseType.Confirm ) {
            maintenanceReservationService.employeeConfirmReservation(reservation, response.getNote());
        }
        else if ( response.getResponse() == MaintenanceNotificationResponseRequest.ResponseType.Reschedule ) {
            maintenanceReservationService.employeeRescheduleReservation(reservation, response.getSlotUnitId(), response.getNote());
        }
        else {
            throw new IllegalArgumentException(String.format("Unsupported response type=%s", response.getResponse().name()));
        }

        return notification;
    }

    private Apartment getApartmentByUnitNumber(@NotNull String targetUnitNumber) {
        Objects.requireNonNull(targetUnitNumber, "Target unit number is null");

        final Apartment apartment = apartmentDao.findByUnitNumberWithinProperty(targetUnitNumber, authorizationManager.getCurrentProperty());
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment with unit number=%s not found", targetUnitNumber));
        }
        return apartment;
    }

    public Notification storeNotificationPhotos(@NotNull MultipartFile[] files, long notificationId) throws IOException {
        Objects.requireNonNull(files, "Files array is null");

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
