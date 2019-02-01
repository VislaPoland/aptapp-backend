package com.creatix.service.notification;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.TenantDao;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * TODO this test should be completely rewritten. Not testing the
 *  PropertyNotificationWatcher but composition of all objects inside
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class PropertyNotificationWatcherTest extends TestContext {

    @Autowired
    private NotificationWatcher notificationWatcher;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private TenantDao tenantDao;
    @Autowired
    private ApartmentDao apartmentDao;

    @Test(expected = AccessDeniedException.class)
    public void testThrottle() throws Exception {
        notificationWatcher.process(mockNotification());
        Thread.sleep(1000);
        notificationWatcher.process(mockNotification());
    }

    private NeighborhoodNotification mockNotification() {
        final NeighborhoodNotification n = new NeighborhoodNotification();
        n.setTargetApartment(apartmentDao.findById(13L));
        n.setCreatedAt(OffsetDateTime.now());
        n.setAuthor(tenantDao.findById(451L));
        n.setProperty(propertyDao.findById(1L));
        n.setTitle("title");

        n.getProperty().setThrottleFastMinutes(1);

        return n;
    }

}
