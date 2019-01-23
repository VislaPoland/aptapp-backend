package com.creatix.service;

import com.creatix.domain.dao.*;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationResponseRequest;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NeighborhoodNotificationResponse;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.template.push.NeighborNotificationNotMeTemplate;
import com.creatix.message.template.push.NeighborNotificationResolvedTemplate;
import com.creatix.message.template.push.NeighborNotificationTemplate;
import com.creatix.message.template.push.PushMessageTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.service.message.PushNotificationSender;
import com.creatix.service.notification.NotificationWatcher;
import freemarker.template.TemplateException;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class NotificationServiceTest {

    private static final long NOTIFICATION_ID = 1L;

    @MockBean
    private  NotificationDao notificationDao;
    @MockBean
    private  MaintenanceNotificationDao maintenanceNotificationDao;
    @MockBean
    private  EscalatedNeighborhoodNotificationDao escalatedNeighborhoodNotificationDao;
    @MockBean
    private  NeighborhoodNotificationDao neighborhoodNotificationDao;
    @MockBean
    private  SecurityNotificationDao securityNotificationDao;
    @MockBean
    private  ApartmentDao apartmentDao;
    @MockBean
    private  AuthorizationManager authorizationManager;
    @MockBean
    private  SmsMessageSender smsMessageSender;
    @MockBean
    private  PushNotificationSender pushNotificationSender;
    @MockBean
    private  SecurityEmployeeDao securityEmployeeDao;
    @MockBean
    private  MaintenanceEmployeeDao maintenanceEmployeeDao;
    @MockBean
    private  MaintenanceReservationService maintenanceReservationService;
    @MockBean
    private  NotificationWatcher notificationWatcher;
    @MockBean
    private  PropertyDao propertyDao;

    @Captor
    private ArgumentCaptor<NeighborNotificationTemplate> notificationTemplateArgumentCaptor;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @SpyBean
    private NotificationService notificationService;

    private EnhancedRandom random;

    @Before
    public void setup() {
        random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .randomizationDepth(1)
                .scanClasspathForConcreteTypes(true)
                .overrideDefaultInitialization(false)
                .build();
    }

    @Test
    public void respondToNeighborhoodNotificationResolved() throws IOException, TemplateException {
        // mocking & stubbing
        Account author = generateTenant(1L);
        Account recipient = generateTenant(2L);

        NeighborhoodNotification neighborhoodNotification = random.nextObject(NeighborhoodNotification.class, "author", "recipient", "respondedAt");
        neighborhoodNotification.setAuthor(author);
        neighborhoodNotification.setRecipient(recipient);

        // mock dao
        when(neighborhoodNotificationDao.findById(any())).thenReturn(neighborhoodNotification);
        doNothing().when(neighborhoodNotificationDao).persist(any());

        // mock sender
        doNothing().when(pushNotificationSender).sendNotification(any(PushMessageTemplate.class), any());

        // mock authorization manager
        when(authorizationManager.isSelf(any())).thenReturn(true);

        // stub request
        NeighborhoodNotificationResponseRequest request = new NeighborhoodNotificationResponseRequest();
        request.setResponse(NeighborhoodNotificationResponse.Resolved);

        NeighborhoodNotification responseNotification = notificationService.respondToNeighborhoodNotification(NOTIFICATION_ID, request);

        // assertions and verifications

        assertEquals(neighborhoodNotification.getId(), responseNotification.getId());
        assertEquals(NotificationStatus.Resolved, responseNotification.getStatus());
        assertEquals(NeighborhoodNotificationResponse.Resolved, responseNotification.getResponse());
        assertNotNull(responseNotification.getRespondedAt());

        InOrder orderedMocks = inOrder(neighborhoodNotificationDao, pushNotificationSender, authorizationManager);
        orderedMocks.verify(neighborhoodNotificationDao).findById(NOTIFICATION_ID);
        orderedMocks.verify(authorizationManager).isSelf(recipient);
        orderedMocks.verify(neighborhoodNotificationDao).persist(any());
        orderedMocks.verify(pushNotificationSender).sendNotification(notificationTemplateArgumentCaptor.capture(), accountArgumentCaptor.capture());
        orderedMocks.verifyNoMoreInteractions();

        NeighborNotificationTemplate notificationTemplate = notificationTemplateArgumentCaptor.getValue();
        assertNotNull(notificationTemplate);
        assertEquals(NeighborNotificationResolvedTemplate.class, notificationTemplate.getClass());

        Account capturedAccount = accountArgumentCaptor.getValue();
        assertNotNull(capturedAccount);
        assertEquals(author.getId(), capturedAccount.getId());
    }

    @Test
    public void respondToNeighborhoodNotificationSorryNotMe() throws IOException, TemplateException {
        // mocking & stubbing
        Account author = generateTenant(1L);
        Account recipient = generateTenant(2L);

        NeighborhoodNotification neighborhoodNotification = random.nextObject(NeighborhoodNotification.class, "author", "recipient", "respondedAt");
        neighborhoodNotification.setAuthor(author);
        neighborhoodNotification.setRecipient(recipient);

        // mock dao
        when(neighborhoodNotificationDao.findById(any())).thenReturn(neighborhoodNotification);
        doNothing().when(neighborhoodNotificationDao).persist(any());

        // mock sender
        doNothing().when(pushNotificationSender).sendNotification(any(PushMessageTemplate.class), any());

        // mock authorization manager
        when(authorizationManager.isSelf(any())).thenReturn(true);

        // stub request
        NeighborhoodNotificationResponseRequest request = new NeighborhoodNotificationResponseRequest();
        request.setResponse(NeighborhoodNotificationResponse.SorryNotMe);

        NeighborhoodNotification responseNotification = notificationService.respondToNeighborhoodNotification(NOTIFICATION_ID, request);

        // assertions and verifications

        assertEquals(neighborhoodNotification.getId(), responseNotification.getId());
        assertEquals(NotificationStatus.Resolved, responseNotification.getStatus());
        assertEquals(NeighborhoodNotificationResponse.SorryNotMe, responseNotification.getResponse());
        assertNotNull(responseNotification.getRespondedAt());

        InOrder orderedMocks = inOrder(neighborhoodNotificationDao, pushNotificationSender, authorizationManager);
        orderedMocks.verify(neighborhoodNotificationDao).findById(NOTIFICATION_ID);
        orderedMocks.verify(authorizationManager).isSelf(recipient);
        orderedMocks.verify(neighborhoodNotificationDao).persist(any());
        orderedMocks.verify(pushNotificationSender).sendNotification(notificationTemplateArgumentCaptor.capture(), accountArgumentCaptor.capture());
        orderedMocks.verifyNoMoreInteractions();

        NeighborNotificationTemplate notificationTemplate = notificationTemplateArgumentCaptor.getValue();
        assertNotNull(notificationTemplate);
        assertEquals(NeighborNotificationNotMeTemplate.class, notificationTemplate.getClass());

        Account capturedAccount = accountArgumentCaptor.getValue();
        assertNotNull(capturedAccount);
        assertEquals(author.getId(), capturedAccount.getId());
    }

    @Test
    public void respondToNeighborhoodNotificationNoIssueFoundShouldNotSendPushNotification() throws IOException, TemplateException {
        // mocking & stubbing
        Account author = generateTenant(1L);
        Account recipient = generateTenant(2L);

        NeighborhoodNotification neighborhoodNotification = random.nextObject(NeighborhoodNotification.class, "author", "recipient", "respondedAt");
        neighborhoodNotification.setAuthor(author);
        neighborhoodNotification.setRecipient(recipient);

        // mock dao
        when(neighborhoodNotificationDao.findById(any())).thenReturn(neighborhoodNotification);
        doNothing().when(neighborhoodNotificationDao).persist(any());

        // mock authorization manager
        when(authorizationManager.isSelf(any())).thenReturn(true);

        // stub request
        NeighborhoodNotificationResponseRequest request = new NeighborhoodNotificationResponseRequest();
        request.setResponse(NeighborhoodNotificationResponse.NoIssueFound);

        NeighborhoodNotification responseNotification = notificationService.respondToNeighborhoodNotification(NOTIFICATION_ID, request);

        // assertions and verifications

        assertEquals(neighborhoodNotification.getId(), responseNotification.getId());
        assertEquals(NotificationStatus.Resolved, responseNotification.getStatus());
        assertEquals(NeighborhoodNotificationResponse.NoIssueFound, responseNotification.getResponse());
        assertNotNull(responseNotification.getRespondedAt());

        InOrder orderedMocks = inOrder(neighborhoodNotificationDao, pushNotificationSender, authorizationManager);
        orderedMocks.verify(neighborhoodNotificationDao).findById(NOTIFICATION_ID);
        orderedMocks.verify(authorizationManager).isSelf(recipient);
        orderedMocks.verify(neighborhoodNotificationDao).persist(any());
        orderedMocks.verify(pushNotificationSender, times(0)).sendNotification(any(PushMessageTemplate.class), any());
        orderedMocks.verifyNoMoreInteractions();
    }

    @Test
    public void respondToNeighborhoodNotificationShouldThrowExceptionWhenNotificationNotFound() throws IOException, TemplateException {
        thrown.expect(EntityNotFoundException.class);
        thrown.expectMessage("Notification id=1 not found");

        // stub request
        NeighborhoodNotificationResponseRequest request = new NeighborhoodNotificationResponseRequest();
        request.setResponse(NeighborhoodNotificationResponse.Resolved);

        when(neighborhoodNotificationDao.findById(any())).thenReturn(null);
        notificationService.respondToNeighborhoodNotification(NOTIFICATION_ID, request);

        verify(neighborhoodNotificationDao).findById(NOTIFICATION_ID);

        // verify nothing else was called
        verify(neighborhoodNotificationDao, times(0)).persist(any());
        verify(authorizationManager, times(0)).isSelf(any());
        verify(pushNotificationSender, times(0)).sendNotification(any(PushMessageTemplate.class), any());
    }

    @Test
    public void respondToNeighborhoodNotificationShouldThrowExceptionWhenTenantNull() throws IOException, TemplateException {
        thrown.expect(SecurityException.class);
        thrown.expectMessage("You are only eligible to respond to notifications targeted at your apartment");

        Account recipient = generateTenant(2L);
        NeighborhoodNotification neighborhoodNotification = random.nextObject(NeighborhoodNotification.class, "author", "recipient", "respondedAt");
        neighborhoodNotification.setRecipient(recipient);

        // stub request
        NeighborhoodNotificationResponseRequest request = new NeighborhoodNotificationResponseRequest();
        request.setResponse(NeighborhoodNotificationResponse.Resolved);

        when(neighborhoodNotificationDao.findById(any())).thenReturn(neighborhoodNotification);
        notificationService.respondToNeighborhoodNotification(NOTIFICATION_ID, request);

        verify(neighborhoodNotificationDao).findById(NOTIFICATION_ID);

        // verify nothing else was called
        verify(neighborhoodNotificationDao, times(0)).persist(any());
        verify(authorizationManager, times(0)).isSelf(any());
        verify(pushNotificationSender, times(0)).sendNotification(any(PushMessageTemplate.class), any());
    }

    @Test
    public void respondToNeighborhoodNotificationShouldThrowExceptionWhenTenantNotSelf() throws IOException, TemplateException {
        thrown.expect(SecurityException.class);
        thrown.expectMessage("You are only eligible to respond to notifications targeted at your apartment");

        Account recipient = generateTenant(2L);
        NeighborhoodNotification neighborhoodNotification = random.nextObject(NeighborhoodNotification.class, "author", "recipient", "respondedAt");
        neighborhoodNotification.setRecipient(recipient);

        // mock authorization manager
        when(authorizationManager.isSelf(any())).thenReturn(false);

        // stub request
        NeighborhoodNotificationResponseRequest request = new NeighborhoodNotificationResponseRequest();
        request.setResponse(NeighborhoodNotificationResponse.Resolved);

        when(neighborhoodNotificationDao.findById(any())).thenReturn(neighborhoodNotification);
        notificationService.respondToNeighborhoodNotification(NOTIFICATION_ID, request);

        verify(neighborhoodNotificationDao).findById(NOTIFICATION_ID);
        verify(authorizationManager).isSelf(recipient);

        // verify nothing else was called
        verify(neighborhoodNotificationDao, times(0)).persist(any());
        verify(pushNotificationSender, times(0)).sendNotification(any(PushMessageTemplate.class), any());
    }

    private Account generateTenant(Long id) {
        Account author = random.nextObject(Tenant.class, "id", "role");
        author.setId(id);
        author.setRole(AccountRole.Tenant);
        return author;
    }
}