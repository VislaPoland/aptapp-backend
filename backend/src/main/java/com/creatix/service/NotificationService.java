package com.creatix.service;

import com.creatix.domain.dao.*;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationResponseRequest;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationResponseRequest;
import com.creatix.domain.dto.notification.security.SecurityNotificationResponseRequest;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.*;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.push.*;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.PushNotificationSender;
import com.creatix.service.notification.NotificationWatcher;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationDao notificationDao;
    private final MaintenanceNotificationDao maintenanceNotificationDao;
    private final EscalatedNeighborhoodNotificationDao escalatedNeighborhoodNotificationDao;
    private final NeighborhoodNotificationDao neighborhoodNotificationDao;
    private final SecurityNotificationDao securityNotificationDao;
    private final ApartmentDao apartmentDao;
    private final AuthorizationManager authorizationManager;
    private final SmsMessageSender smsMessageSender;
    private final PushNotificationSender pushNotificationSender;
    private final SecurityEmployeeDao securityEmployeeDao;
    private final MaintenanceEmployeeDao maintenanceEmployeeDao;
    private final MaintenanceReservationService maintenanceReservationService;
    private final NotificationWatcher notificationWatcher;
    private final PropertyDao propertyDao;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }


    public PageableDataResponse<List<Notification>> filterNotifications(
            @Nonnull NotificationRequestType requestType,
            @Nullable NotificationStatus[] notificationStatuses,
            @Nullable NotificationType[] notificationTypes,
            @Nullable Long startId,
            @Nullable Long propertyId,
            int pageSize,
            SortEnum order) {
        Objects.requireNonNull(requestType, "Request type is null");

        final Account account = authorizationManager.getCurrentAccount();

        // HACK: pls remove!!!
        pageSize = 9999;

        List<Notification> notifications = notificationDao.findPageByNotificationStatusAndNotificationTypeAndRequestTypeAndAccount(
                requestType,
                notificationStatuses,
                notificationTypes,
                startId,
                account,
                findPropertyById(propertyId),
                pageSize + 1);

        if (order != null && notificationTypes != null && notificationTypes.length == 1 && NotificationType.Maintenance.equals(notificationTypes[0])) {
            sortMaintenanceNotifications(order, notifications);
        }

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

    private void sortMaintenanceNotifications(@Nonnull SortEnum order, List<Notification> notifications) {
        switch (order) {
            case ASC:
                notifications.sort((m1, m2) -> getDateForCompare((MaintenanceNotification) m2).compareTo(getDateForCompare((MaintenanceNotification) m1)));
                Collections.reverse(notifications);
                break;
            case DESC:
                notifications.sort(Comparator.comparing(Notification::getUpdatedAt));
                Collections.reverse(notifications);
                break;
            default:
                throw new IllegalArgumentException("Illegal argument in order.");

        }
    }

    private OffsetDateTime getDateForCompare(MaintenanceNotification maintenance) {
        switch (maintenance.getReservations().size()) {
            case 0:
                return maintenance.getUpdatedAt();
            case 1:
                return maintenance.getReservations().get(0).getBeginTime();
            default:
                return latestReservation(maintenance);
        }
    }

    private OffsetDateTime latestReservation(MaintenanceNotification maintenance) {
        List<OffsetDateTime> latestReservation = maintenance.getReservations().stream()
            .filter(res ->  !ReservationStatus.Rescheduled.equals(res.getStatus()))
            .map(MaintenanceReservation::getBeginTime)
            .sorted()
            .collect(toList());

        return latestReservation.get(0);
    }

    /**
     * this method is @deprecated use {@link com.creatix.service.notification.NotificationReportService#getReportsByRange(OffsetDateTime, OffsetDateTime, NotificationType, Long)}
     * with {@link NotificationType#Maintenance} as the third input parameter
     *
     * @param beginDate left value of datetime range
     * @param endDate right value of datetime range
     * @return list of MaintenanceNotification type
     */
    @Deprecated
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
    public MaintenanceNotification saveMaintenanceNotification(String targetUnitNumber, @Nonnull MaintenanceNotification notification, @Deprecated @Nonnull Long slotUnitId, List<Long> slotsUnitId, @Nullable Long propertyId) throws IOException, TemplateException {
        Objects.requireNonNull(notification, "Notification is null");

// TODO uncomment this after deleting deprecated "slotUnitId"
//        Objects.requireNonNull(slotsUnitId, "slot unit id is null");

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
        maintenanceNotificationDao.createNotificationHistoryLog(notification, NotificationHistoryStatus.Pending.name());

        // TODO delete original slotUnitId after FE and APP update and release
        if (slotsUnitId != null && slotsUnitId.size() > 0) {
            for (Long slotUnitIdFromList : slotsUnitId) {
                maintenanceReservationService.createMaintenanceReservation(notification, slotUnitIdFromList);
            }
        } else {
            maintenanceReservationService.createMaintenanceReservation(notification, slotUnitId);
        }

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

        if (currentAccount.getIsNeighborhoodNotificationEnable() != null && !currentAccount.getIsNeighborhoodNotificationEnable()) {
            throw new AccessDeniedException("You have been blocked to send any notification messages to your neighbors. To unblock sending the notifications, contact your property manager.");
        }

        final Apartment targetApartment = getApartmentByUnitNumber(targetUnitNumber, propertyId);
        final Property property = targetApartment.getProperty();
        authorizationManager.checkRead(property);

        final Tenant tenant = targetApartment.getTenant();

        if (tenant == null) {
            throw new AccessDeniedException("Selected apartment is currently unoccupied.");
        }

        notification.setType(NotificationType.Neighborhood);
        notification.setAuthor(currentAccount);
        notification.setProperty(property);
        notification.setStatus(NotificationStatus.Pending);
        notification.setRecipient(targetApartment.getTenant());
        notification.setTargetApartment(targetApartment);
        neighborhoodNotificationDao.persist(notification);

        if ( AccountRole.Tenant.equals(currentAccount.getRole()) || AccountRole.SubTenant.equals(currentAccount.getRole())) {
            notificationWatcher.process(notification);
        }

        if ( Boolean.TRUE.equals(property.getEnableSms()) && Boolean.TRUE.equals(tenant.getEnableSms()) && StringUtils.isNotBlank(tenant.getPrimaryPhone()) ) {
            try {
                smsMessageSender.send(new com.creatix.message.template.sms.NeighborNotificationTemplate(tenant));
            } catch (Exception e) {
                log.info(String.format("Failed to sms notify %s", tenant.getPrimaryEmail()), e);
            }
        }
        pushNotificationSender.sendNotification(new NeighborNotificationTemplate(notification), tenant);

        return notification;
    }

    @RoleSecured(value = AccountRole.Tenant)
    public NeighborhoodNotification respondToNeighborhoodNotification(long notificationId, @Nonnull NeighborhoodNotificationResponseRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(request, "Notification response request is null");

        final NeighborhoodNotification notification = getOrElseThrow(notificationId, neighborhoodNotificationDao,
                new EntityNotFoundException(String.format("Notification id=%d not found", notificationId)));

        final Tenant tenant = notification.getRecipientAsTenant();

        if(tenant == null || !authorizationManager.isSelf(tenant)) {
            throw new SecurityException("You are only eligible to respond to notifications targeted at your apartment");
        }

        notification.setStatus(NotificationStatus.Resolved);
        notification.setResponse(request.getResponse());
        notification.setRespondedAt(OffsetDateTime.now());
        neighborhoodNotificationDao.persist(notification);

        if ( request.getResponse() == NeighborhoodNotificationResponse.Resolved ) {
            pushNotificationSender.sendNotification(new NeighborNotificationResolvedTemplate(notification), notification.getAuthor());
        }
        else if ( request.getResponse() == NeighborhoodNotificationResponse.SorryNotMe ) {
            pushNotificationSender.sendNotification(new NeighborNotificationNotMeTemplate(notification), notification.getAuthor());
        }

        return notification;
    }

    @RoleSecured(value = {AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public NeighborhoodNotification respondToEscalatedNeighborhoodNotification(long notificationId, @Nonnull NeighborhoodNotificationResponseRequest request) throws IOException, TemplateException {
        Objects.requireNonNull(request, "Notification response request is null");

        final List<EscalatedNeighborhoodNotification> notifications = escalatedNeighborhoodNotificationDao.findByNotificationGroup(notificationId);

        if ( notifications != null && !notifications.isEmpty() &&
                (authorizationManager.isManager(notifications.get(0).getProperty()) || AccountRole.Administrator.equals(authorizationManager.getCurrentAccount().getRole()))) {

            notifications.forEach(escalatedNeighborhoodNotification -> {
                escalatedNeighborhoodNotification.setStatus(NotificationStatus.Resolved);
                escalatedNeighborhoodNotification.setResponse(request.getResponse());
                escalatedNeighborhoodNotification.setRespondedAt(OffsetDateTime.now());
                escalatedNeighborhoodNotification.setClosedAt(OffsetDateTime.now());
                escalatedNeighborhoodNotificationDao.persist(escalatedNeighborhoodNotification);
            });

            EscalatedNeighborhoodNotification notification = notifications.stream().filter(escalatedNeighborhoodNotification -> escalatedNeighborhoodNotification.getId().equals(notificationId)).findAny().orElse(null);

            assert notification != null;

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
    public MaintenanceNotification closeMaintenanceNotification(@Nonnull Long notificationId) {

        final MaintenanceNotification notification = getMaintenanceNotification(notificationId);
        notification.setClosedAt(OffsetDateTime.now());
        notification.setStatus(NotificationStatus.Closed);

        maintenanceNotificationDao.persist(notification);
        maintenanceNotificationDao.createNotificationHistoryLog(notification, NotificationHistoryStatus.Closed.name());

        try {
            pushNotificationSender.sendNotification(new MaintenanceCompleteTemplate(notification), notification.getAuthor());
        } catch (IOException | TemplateException e) {
            log.error("Problem with sending push notification for closing maintenance notification.", e);
        }

        return notification;
    }

    @RoleSecured({AccountRole.Maintenance, AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public MaintenanceNotification deleteMaintenanceNotificationAndNotify(@Nonnull Long notificationId) {

        final MaintenanceNotification notification = getMaintenanceNotification(notificationId);

        releaseFutureReservationIgnorePastReservation(notification);
        notification.setClosedAt(OffsetDateTime.now());
        notification.setStatus(NotificationStatus.Deleted);

        maintenanceNotificationDao.persist(notification);
        maintenanceNotificationDao.createNotificationHistoryLog(notification, NotificationHistoryStatus.Deleted.name());

        if (AccountRole.Tenant.equals(authorizationManager.getCurrentAccount().getRole()) ||
                AccountRole.SubTenant.equals(authorizationManager.getCurrentAccount().getRole())) {
            sendPushNotificationToMaintener(notification);
        }

        return notification;
    }

    private void sendPushNotificationToMaintener(MaintenanceNotification notification) {
        List<MaintenanceEmployee> maintenanceEmployeeList = maintenanceEmployeeDao.findByProperty(authorizationManager.getCurrentProperty());
        maintenanceEmployeeList.forEach(maintenanceEmployee -> {
            try {
                pushNotificationSender.sendNotification(new MaintenanceDeleteTemplate(notification, authorizationManager.getCurrentAccount()), maintenanceEmployee);
            } catch (IOException | TemplateException e) {
                log.error("Problem with sending push notification for deleting maintenance notification.", e);
            }
        });
    }

    private void releaseFutureReservationIgnorePastReservation(MaintenanceNotification notification) {
        notification.getReservations().forEach(maintenanceReservation -> {
            if (maintenanceReservation.getBeginTime().isAfter(OffsetDateTime.now())) {
                maintenanceReservationService.deleteById(maintenanceReservation.getId());
            }
        });
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
