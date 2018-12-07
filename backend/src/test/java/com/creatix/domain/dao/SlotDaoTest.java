package com.creatix.domain.dao;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.Slot;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    private AccountDao accountDao;

    private static final ZoneOffset LOCAL_ZONE_OFFSET = ZoneOffset.ofHours(TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (7200 * 1000));

    @Test
    public void findByPropertyAndAccountAndBeginTime() throws Exception {
        final int pageSize = 2;
        final Property property = propertyDao.findById(1L);
        assertNotNull(property);

        List<Slot> slots = slotDao.findByPropertyAndAccountAndBeginTime(
                property,
                accountDao.findByEmail("apt@test.com"),
                OffsetDateTime.of(2016, 2, 1, 4, 0, 0, 0, LOCAL_ZONE_OFFSET),
                pageSize
        );
        assertNotNull(slots);
        assertEquals(pageSize, slots.size());


        slots = slotDao.findByPropertyAndAccountAndBeginTime(
                property,
                accountDao.findByEmail("apt@test.com"),
                OffsetDateTime.of(2016, 2, 1, 4, 0, 0, 0, LOCAL_ZONE_OFFSET),
                9999
        );
        assertNotNull(slots);
        assertEquals(9, slots.size());


        slots = slotDao.findByPropertyAndAccountAndBeginTime(
                property,
                accountDao.findByEmail("tomas.sedlak@thinkcreatix.com"),
                OffsetDateTime.of(2016, 2, 1, 4, 0, 0, 0, LOCAL_ZONE_OFFSET),
                9999
        );
        assertNotNull(slots);
        assertEquals(9, slots.size());


        slots = slotDao.findByPropertyAndAccountAndBeginTime(
                property,
                accountDao.findByEmail("martin.maintenance@apartments.com"),
                OffsetDateTime.of(2016, 2, 1, 4, 0, 0, 0, LOCAL_ZONE_OFFSET),
                9999
        );
        assertNotNull(slots);
        assertEquals(9, slots.size());


        slots = slotDao.findByPropertyAndAccountAndBeginTime(
                property,
                accountDao.findByEmail("martin.security@apartments.com"),
                OffsetDateTime.of(2016, 2, 1, 4, 0, 0, 0, LOCAL_ZONE_OFFSET),
                9999
        );
        assertNotNull(slots);
        assertEquals(8, slots.size());
    }


    @Test
    public void findByPropertyAndAccountAndDateRange() {
        final Property property = propertyDao.findById(1L);
        assertNotNull(property);

        List<Slot> slots = slotDao.findByPropertyAndAccountAndDateRange(
                property,
                accountDao.findByEmail("martin.security@apartments.com"),
                OffsetDateTime.of(2016, 7, 18, 0, 0, 0, 0, LOCAL_ZONE_OFFSET),
                OffsetDateTime.of(2016, 7, 18, 23, 59, 59, 999, LOCAL_ZONE_OFFSET));
        assertNotNull(slots);
        assertEquals(0, slots.size());

        slots = slotDao.findByPropertyAndAccountAndDateRange(
                property,
                accountDao.findByEmail("martin.maintenance@apartments.com"),
                OffsetDateTime.of(2016, 2, 1, 0, 0, 0, 0, LOCAL_ZONE_OFFSET),
                OffsetDateTime.of(2016, 2, 1, 23, 59, 59, 999, LOCAL_ZONE_OFFSET));
        assertNotNull(slots);
        assertEquals(4, slots.size());
    }


    @Test
    public void findByPropertyAndAccountAndSlotIdGreaterOrEqual() {
        final Property property = propertyDao.findById(1L);
        assertNotNull(property);

        List<Slot> slots = slotDao.findByPropertyAndAccountAndSlotIdGreaterOrEqual(
                property,
                accountDao.findByEmail("tomas.sedlak@thinkcreatix.com"),
                102L,
                1);
        assertNotNull(slots);
        assertEquals(1, slots.size());

        slots = slotDao.findByPropertyAndAccountAndSlotIdGreaterOrEqual(
                property,
                accountDao.findByEmail("tomas.sedlak@thinkcreatix.com"),
                200L,
                1);
        assertNotNull(slots);
        assertEquals(1, slots.size());

        slots = slotDao.findByPropertyAndAccountAndSlotIdGreaterOrEqual(
                property,
                accountDao.findByEmail("tomas.sedlak@thinkcreatix.com"),
                201L,
                1);
        assertNotNull(slots);
        assertEquals(1, slots.size());

        slots = slotDao.findByPropertyAndAccountAndSlotIdGreaterOrEqual(
                property,
                accountDao.findByEmail("martin.security@apartments.com"),
                201L,
                1);
        assertNotNull(slots);
        assertEquals(1, slots.size());
    }
}