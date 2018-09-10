package com.creatix.service;

import com.creatix.domain.dao.*;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationResponseRequest;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationResponseRequest;
import com.creatix.domain.dto.notification.security.SecurityNotificationResponseRequest;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.*;
import com.creatix.message.MessageDeliveryException;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.push.*;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.PushNotificationSender;
import com.creatix.service.notification.NotificationWatcher;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.OffsetDateTime;
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
    private EscalatedNeighborhoodNotificationDao escalatedNeighborhoodNotificationDao;
    @Autowired
    private NeighborhoodNotificationDao neighborhoodNotificationDao;
    @Autowired
    private SecurityNotificationDao securityNotificationDao;
    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private AuthorizationManager authorizationManager;
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
    @Autowired
    private NotificationWatcher notificationWatcher;
    @Autowired
    private PropertyDao propertyDao;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }


    public PageableDataResponse<List<Notification>> filterNotifications(
            @Nonnull NotificationRequestType requestType,
            @Nullable NotificationStatus[] notificationStatus,
            @Nullable NotificationType[] notificationType,
            @Nullable Long startId,
            @Nullable Long propertyId,
            int pageSize) {
        Objects.requireNonNull(requestType, "Request type is null");

        final Account account = authorizationManager.getCurrentAccount();

        List<Notification> notifications = notificationDao.findPageByNotificationStatusAndNotificationTypeAndRequestTypeAndAccount(
                requestType,
                notificationStatus,
                notificationType,
                startId,
                account,
                findPropertyById(propertyId),
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

    public List<MaintenanceNotification> getAllMaintenanceNotificationsInDateRange(@Nonnull OffsetDateTime beginDate, @Nonnull OffsetDateTime endDate) {
        Objects.requireNonNull(beginDate, "Begin date is null");
        Objects.requireNonNull(endDate, "End date is null");
        return maintenanceNotificationDao.findAllInDateRange(beginDate, endDate);
    }

    public SecurityNotification getSecurityNotification(@Nonnull Long notificationId) {
        Objects.requireNonNull(notificationId, "Notification id is null");

        return getOrElseThrow(notificationId, securityNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public MaintenanceNotification getMaintenanceNotification(@Nonnull Long notificationId) {
        Objects.requireNonNull(notificationId, "Notification id is null");

        return getOrElseThrow(notificationId, maintenanceNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public NeighborhoodNotification getNeighborhoodNotification(@Nonnull Long notificationId) {
        Objects.requireNonNull(notificationId, "Notification id is null");

        return getOrElseThrow(notificationId, neighborhoodNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));
    }

    public SecurityNotification saveSecurityNotification(@Nonnull SecurityNotification notification, @Nullable Long propertyId) throws IOException, TemplateException {
        Objects.requireNonNull(notification, "Notification is null");
        notification.setType(NotificationType.Security);
        notification.setAuthor(authorizationManager.getCurrentAccount());
        notification.setProperty(propertyId != null ? propertyDao.findById(propertyId) : authorizationManager.getCurrentProperty());
        notification.setStatus(NotificationStatus.Pending);
        securityNotificationDao.persist(notification);

        for ( SecurityEmployee secEmp : securityEmployeeDao.findByProperty(notification.getProperty()) ) {
            pushNotificationSender.sendNotification(new SecurityNotificationTemplate(notification), secEmp);
        }

        return notification;
    }

    @RoleSecured
    public MaintenanceNotification saveMaintenanceNotification(String targetUnitNumber, @Nonnull MaintenanceNotification notification, @Nonnull Long slotUnitId, @Nullable Long propertyId) throws IOException, TemplateException {
        Objects.requireNonNull(notification, "Notification is null");
        Objects.requireNonNull(slotUnitId, "slot unit id is null");

        Account currentAccount = authorizationManager.getCurrentAccount();

        notification.setType(NotificationType.Maintenance);
        notification.setAuthor(currentAccount);
        if (propertyId == null) {
            notification.setProperty(authorizationManager.getCurrentProperty());
        } else {
            notification.setProperty(findPropertyById(propertyId));
        }
        notification.setStatus(NotificationStatus.Pending);
        switch (currentAccount.getRole()) {
            case Tenant:
                notification.setTargetApartment(((Tenant) currentAccount).getApartment());
                break;
            case SubTenant:
                notification.setTargetApartment(((SubTenant) currentAccount).getApartment());
                break;
            case Administrator:
            case Maintenance:
            case PropertyManager:
            case AssistantPropertyManager:
                if (null != targetUnitNumber) {
                    notification.setTargetApartment(getApartmentByUnitNumber(targetUnitNumber, propertyId));
                }
                break;
            default:
                break;
        }
        maintenanceNotificationDao.persist(notification);

        maintenanceReservationService.createMaintenanceReservation(notification, slotUnitId);

        for ( MaintenanceEmployee employee : maintenanceEmployeeDao.findByProperty(notification.getProperty()) ) {
            pushNotificationSender.sendNotification(new MaintenanceNotificationTemplate(notification), employee);
        }

        if ( authorizationManager.hasAnyOfRoles(AccountRole.Maintenance) ) {
            // automatically confirm maintenance reservation when created by maintenance employee
            final MaintenanceNotificationResponseRequest response = new MaintenanceNotificationResponseRequest();
            response.setResponse(MaintenanceNotificationResponseRequest.ResponseType.Confirm);
            response.setNote("Created by maintenance employee");
            return maintenanceReservationService.employeeRespondToMaintenanceNotification(notification, response);
        }
        else {
            return notification;
        }
    }

    public NeighborhoodNotification saveNeighborhoodNotification(@Nonnull String targetUnitNumber, @Nonnull NeighborhoodNotification notification, Long propertyId) throws TemplateException, IOException {
        Objects.requireNonNull(targetUnitNumber, "Target unit number is null");
        Objects.requireNonNull(notification, "Notification is null");

        Account currentAccount = authorizationManager.getCurrentAccount();

        if (currentAccount.getIsNeighborhoodNotificationEnable() != null && currentAccount.getIsNeighborhoodNotificationEnable() != true) {
            throw new AccessDeniedException("You have been blocked to send any notification messages to your neighbors. To unblock sending the notifications, contact your property manager.");
        }

        final Apartment targetApartment = getApartmentByUnitNumber(targetUnitNumber, propertyId);
        final Property property = targetApartment.getProperty();
        authorizationManager.checkRead(property);

        final Tenant tenant = targetApartment.getTenant();
        if ( tenant != null ) {

            notification.setType(NotificationType.Neighborhood);
            notification.setAuthor(currentAccount);
            notification.setProperty(property);
            notification.setStatus(NotificationStatus.Pending);
            notification.setRecipient(targetApartment.getTenant());
            notification.setTargetApartment(targetApartment);
            if ( AccountRole.Tenant.equals(currentAccount.getRole()) || AccountRole.SubTenant.equals(currentAccount.getRole())) {
                notificationWatcher.process(notification);
            }
            neighborhoodNotificationDao.persist(notification);

            if ( (property.getEnableSms() == Boolean.TRUE) && (tenant.getEnableSms() == Boolean.TRUE) && (StringUtils.isNotBlank(tenant.getPrimaryPhone())) ) {
                try {
                    smsMessageSender.send(new com.creatix.message.template.sms.NeighborNotificationTemplate(tenant));
                }
                catch ( Exception e ) {
                    logger.info(String.format("Failed to sms notify %s", tenant.getPrimaryEmail()), e);
                }
            }
            pushNotificationSender.sendNotification(new NeighborNotificationTemplate(notification), tenant);
        }

        return notification;
    }

    @RoleSecured(value = AccountRole.Tenant)
    public NeighborhoodNotification respondToNeighborhoodNotification(long notificationId, @Nonnull NeighborhoodNotificationResponseRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(request, "Notification response request is null");

        final NeighborhoodNotification notification = getOrElseThrow(notificationId, neighborhoodNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));

        final Tenant tenant = notification.getRecipientAsTenant();
        if ( tenant != null ) {
            if ( authorizationManager.isSelf(tenant) ) {
                notification.setStatus(NotificationStatus.Resolved);
                notification.setResponse(request.getResponse());
                notification.setRespondedAt(OffsetDateTime.now());
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

    @RoleSecured(value = {AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public NeighborhoodNotification respondToEscalatedNeighborhoodNotification(long notificationId, @Nonnull NeighborhoodNotificationResponseRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(request, "Notification response request is null");

        final List<EscalatedNeighborhoodNotification> notifications = escalatedNeighborhoodNotificationDao.findByNotificationGroup(notificationId);

        if ( notifications != null && !notifications.isEmpty() &&
                (authorizationManager.isManager(notifications.get(0).getProperty()) || AccountRole.Administrator.equals(authorizationManager.getCurrentAccount().getRole()))) {

            notifications.stream().forEach(escalatedNeighborhoodNotification -> {
                escalatedNeighborhoodNotification.setStatus(NotificationStatus.Resolved);
                escalatedNeighborhoodNotification.setResponse(request.getResponse());
                escalatedNeighborhoodNotification.setRespondedAt(OffsetDateTime.now());
                escalatedNeighborhoodNotification.setClosedAt(OffsetDateTime.now());
                escalatedNeighborhoodNotificationDao.persist(escalatedNeighborhoodNotification);
            });

            EscalatedNeighborhoodNotification notification = notifications.stream().filter(escalatedNeighborhoodNotification -> escalatedNeighborhoodNotification.getId().equals(notificationId)).findAny().orElse(null);

            if ( request.getResponse() == NeighborhoodNotificationResponse.Resolved ) {
                pushNotificationSender.sendNotification(new EscalatedNeighborNotificationResolvedTemplate(notification), notification.getAuthor());
            }
            else if ( request.getResponse() == NeighborhoodNotificationResponse.NoIssueFound ) {
                pushNotificationSender.sendNotification(new EscalatedNeighborNotificationNoIssueFoundTemplate(notification), notification.getAuthor());
            }

            return notification;
        }
        throw new SecurityException("You are only eligible to respond to notifications targeted at your apartment");
    }

    @RoleSecured(value = {AccountRole.Security})
    public SecurityNotification respondToSecurityNotification(long notificationId, @Nonnull SecurityNotificationResponseRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(request, "Notification response request is null");

        final SecurityNotification notification = getOrElseThrow(notificationId, securityNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));

        if ( notification.getProperty().equals(authorizationManager.getCurrentProperty()) ||
                AccountRole.Administrator.equals(authorizationManager.getCurrentAccount().getRole()) ) {
            notification.setResponse(request.getResponse());
            notification.setRespondedAt(OffsetDateTime.now());
            notification.setStatus(NotificationStatus.Resolved);
            securityNotificationDao.persist(notification);

            if ( request.getResponse() == SecurityNotificationResponseType.NoIssueFound ) {
                final Account account = notification.getAuthor();

                if ( account.getRole() == AccountRole.Tenant || account.getRole() == AccountRole.SubTenant ) {
                    pushNotificationSender.sendNotification(new SecurityNotificationNeighborNoIssueTemplate(notification), account);
                }
                else if ( account.getRole() == AccountRole.PropertyManager || account.getRole() == AccountRole.AssistantPropertyManager ) {
                    pushNotificationSender.sendNotification(new SecurityNotificationManagerNoIssueTemplate(notification), account);
                }
            }
            else if ( request.getResponse() == SecurityNotificationResponseType.Resolved ) {
                final Account account = notification.getAuthor();

                if ( account.getRole() == AccountRole.Tenant || account.getRole() == AccountRole.SubTenant ) {
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

    @RoleSecured({AccountRole.Administrator, AccountRole.Maintenance, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public MaintenanceNotification respondToMaintenanceNotification(@Nonnull Long notificationId, @Nonnull MaintenanceNotificationResponseRequest response) throws IOException, TemplateException {
        final MaintenanceNotification notification = getMaintenanceNotification(notificationId);

        if ( authorizationManager.hasAnyOfRoles(AccountRole.Maintenance) ) {
            return maintenanceReservationService.employeeRespondToMaintenanceNotification(notification, response);
        }
        else if ( authorizationManager.hasAnyOfRoles(AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager) ) {
            return maintenanceReservationService.tenantRespondToMaintenanceReschedule(notification, response);
        }
        else {
            throw new IllegalStateException("Unsupported account role");
        }
    }

    @RoleSecured(AccountRole.Maintenance)
    public MaintenanceNotification closeMaintenanceNotification(@Nonnull Long notificationId) throws IOException, TemplateException {

        final MaintenanceNotification notification = getMaintenanceNotification(notificationId);
        notification.setClosedAt(OffsetDateTime.now());
        notification.setStatus(NotificationStatus.Closed);

        maintenanceNotificationDao.persist(notification);

        return notification;
    }

    private Apartment getApartmentByUnitNumber(@Nonnull String targetUnitNumber, Long propertyId) {
        Objects.requireNonNull(targetUnitNumber, "Target unit number is null");

        Property property = (propertyId != null) ? propertyDao.findById(propertyId) : authorizationManager.getCurrentProperty();

        final Apartment apartment = apartmentDao.findByUnitNumberWithinProperty(targetUnitNumber, property);
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment with unit number=%s not found", targetUnitNumber));
        }
        return apartment;
    }

    @Nullable
    private Property findPropertyById(@Nullable Long propertyId) {
        if (propertyId == null) {
            return null;
        }

        Property property = propertyDao.findById(propertyId);

        if (null == property) {
            throw new EntityNotFoundException(String.format("Property %d not found", propertyId));
        }
        return property;
    }
}
