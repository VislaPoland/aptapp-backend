package com.creatix.service;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.entity.store.notification.Notification;
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

    @Test
    public void storeNotificationPhotos() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", new byte[] {1,2,3,4,5,6});

        final Notification notification = notificationService.storeNotificationPhotos(new MultipartFile[]{file}, 1);
        assertNotNull(notification);
        assertEquals((Long) 1L, notification.getId());
        assertNotNull(notification.getPhotos());
        assertEquals(1, notification.getPhotos().size());
        assertNotNull(notification.getPhotos().get(0).getId());
        assertEquals("test.jpg", notification.getPhotos().get(0).getFileName());
    }

}