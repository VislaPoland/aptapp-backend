package com.creatix.service;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.dto.property.slot.ScheduledSlotsResponse;
import com.creatix.domain.dto.property.slot.SlotDto;
import com.creatix.mock.WithMockCustomUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class SlotServiceTest {

    @Autowired
    private SlotService slotService;

    @Test
    @WithMockCustomUser("apt@test.com")
    public void getSlotsByFilter() throws Exception {
        ScheduledSlotsResponse result;
        final Long propertyId = 1L;
        LocalDate beginDate = LocalDate.of(2016, 7, 1);
        LocalDate endDate = LocalDate.of(2016, 8, 31);
        result = slotService.getSlotsByFilter(propertyId, beginDate, endDate, null, null);
        assertNotNull(result);
        assertEquals(3, result.getSlots().size());

        Long startId = 100L;
        int pageSize = 4;
        result = slotService.getSlotsByFilter(propertyId, null, null, startId, pageSize);
        assertNotNull(result);
        assertEquals(pageSize, result.getSlots().size());
        assertEquals(startId, result.getSlots().get(0).getId());
        assertEquals(104L, (long) result.getNextId());

        startId = 421L;
        result = slotService.getSlotsByFilter(propertyId, null, null, startId, pageSize);
        assertNotNull(result);
        assertEquals(2, result.getSlots().size());
        assertEquals(startId, result.getSlots().get(0).getId());
        assertNull(result.getNextId());

        result.getSlots().forEach(s -> assertTrue(s.getType() == SlotDto.SlotType.Event));
    }

}