package com.creatix.service;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.notification.neighborhood.CreateNeighborhoodNotificationRequest;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.mock.WithMockCustomUser;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private StoredFilesService storedFilesService;
    @Autowired
    private Mapper mapper;

    @Test
    public void storeNotificationPhotos() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", new byte[] {1,2,3,4,5,6});

        final Notification notification = storedFilesService.storeNotificationPhotos(new MultipartFile[]{file}, 1);
        assertNotNull(notification);
        assertEquals((Long) 1L, notification.getId());
        assertNotNull(notification.getPhotos());
        assertEquals(1, notification.getPhotos().size());
        assertNotNull(notification.getPhotos().get(0).getId());
        assertTrue(StringUtils.endsWith(notification.getPhotos().get(0).getFileName(), "test.jpg"));
    }

    @Test
    @WithMockCustomUser("apt@test.com")
    public void saveNeighborhoodNotification() throws Exception {
        final CreateNeighborhoodNotificationRequest request = new CreateNeighborhoodNotificationRequest();
        request.setDate(new Date());
        request.setTitle("Complaint");
        request.setDescription("Your dog is too loud!");
        request.setUnitNumber("21");
        final NeighborhoodNotification notification = notificationService.saveNeighborhoodNotification(request.getUnitNumber(), mapper.fromNeighborhoodNotificationRequest(request));
        assertNotNull(notification);
        assertNotNull(notification.getId());
        assertEquals("Complaint", notification.getTitle());
        assertEquals("Your dog is too loud!", notification.getDescription());
        assertTrue((notification.getPhotos() == null) || notification.getPhotos().isEmpty());
    }
}