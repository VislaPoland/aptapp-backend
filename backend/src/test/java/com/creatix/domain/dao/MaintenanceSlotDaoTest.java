package com.creatix.domain.dao;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.entity.store.MaintenanceSlot;
import com.creatix.domain.entity.store.Property;
import com.creatix.mock.WithMockCustomUser;
import com.creatix.security.AuthorizationManager;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Component
public class MaintenanceSlotDaoTest {

    @Autowired
    private MaintenanceSlotDao slotDao;
    @Autowired
    private PropertyDao propertyDao;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private AuthorizationManager authorizationManager;

    private final OffsetDateTime beginDt = LocalDateTime.of(2016, 5, 1, 10, 0, 0, 0).atOffset(ZoneOffset.ofHours(-2));
    private final OffsetDateTime endDt = LocalDateTime.of(2016, 5, 1, 10, 0, 0, 0).atOffset(ZoneOffset.ofHours(3));

    @Test
    @Transactional
    @Commit
    public void testPersist() {

        final Property property = propertyDao.findById(1L);
        assertNotNull(property);

        MaintenanceSlot s = new MaintenanceSlot();
        s.setProperty(property);
        s.setUnitDurationMinutes(30);
        s.setBeginTime(beginDt);
        s.setEndTime(endDt);
        slotDao.persist(s);
        assertSame(beginDt, s.getBeginTime());
        assertSame(endDt, s.getEndTime());
        assertNotNull(s.getId());

        em.flush();
        em.detach(s);
        s.setBeginTime(null);
        s.setEndTime(null);

        assertNotNull(s.getId());
        s = slotDao.findById(s.getId());
        assertNotNull(s);
        assertNotSame(beginDt, s.getBeginTime());
        assertEquals(beginDt.atZoneSameInstant(ZoneId.systemDefault()), s.getBeginTime().atZoneSameInstant(ZoneId.systemDefault()));
        assertNotSame(endDt, s.getEndTime());
        assertEquals(endDt.atZoneSameInstant(ZoneId.systemDefault()), s.getEndTime().atZoneSameInstant(ZoneId.systemDefault()));
    }

}