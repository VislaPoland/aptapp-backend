package com.creatix.domain.dao;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.entity.store.MaintenanceSlot;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.Slot;
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
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Transactional
@Component
public class SlotDaoTest {

    @Autowired
    private SlotDao slotDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private AuthorizationManager authorizationManager;

    @Test
    @WithMockCustomUser("apt@test.com")
    public void findByPropertyAndBeginTimeAndAccount() throws Exception {
        final int pageSize = 2;
        final Property property = propertyDao.findById(1L);
        assertNotNull(property);

        final List<Slot> slots = slotDao.findByPropertyAndBeginTimeAndAccount(
                property,
                OffsetDateTime.of(2016, 2, 1, 4, 0, 0, 0, ZoneOffset.UTC),
                pageSize,
                authorizationManager.getCurrentAccount());
        assertNotNull(slots);
        assertEquals(pageSize, slots.size());
    }

}