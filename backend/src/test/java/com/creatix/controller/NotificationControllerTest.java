package com.creatix.controller;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.controller.v1.NotificationController;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationResponseRequest;
import com.creatix.domain.dto.property.slot.MaintenanceReservationDto;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.ReservationStatus;
import com.creatix.mock.WithMockCustomUser;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class NotificationControllerTest {

    @Autowired
    private NotificationController notificationController;

    @Test
    @WithMockCustomUser("martin.maintenance@apartments.com")
    public void respondToMaintenanceNotificationConfirm() throws Exception {
        final MaintenanceNotificationResponseRequest request = new MaintenanceNotificationResponseRequest();
        request.setResponse(MaintenanceNotificationResponseRequest.ResponseType.Confirm);
        final DataResponse<MaintenanceNotificationDto> response = notificationController.respondToMaintenanceNotification(203L, request);
        assertNotNull(response);
        assertNotNull(response.getData());
        final MaintenanceNotificationDto responseData = response.getData();
        assertEquals(NotificationStatus.Resolved, responseData.getStatus());
        assertNotNull(responseData.getReservations());
        assertEquals(1, responseData.getReservations().size());
        final MaintenanceReservationDto reservationDto = responseData.getReservations().get(0);
        assertNotNull(reservationDto.getNotification());
        assertEquals(ReservationStatus.Confirmed, reservationDto.getStatus());
    }

    @Test
    @WithMockCustomUser("martin.maintenance@apartments.com")
    public void respondToMaintenanceNotificationReschedule() throws Exception {
        final MaintenanceNotificationResponseRequest request = new MaintenanceNotificationResponseRequest();
        request.setResponse(MaintenanceNotificationResponseRequest.ResponseType.Reschedule);
        request.setSlotUnitId(548L);
        final DataResponse<MaintenanceNotificationDto> response = notificationController.respondToMaintenanceNotification(203L, request);
        assertNotNull(response);
        assertNotNull(response.getData());
        final MaintenanceNotificationDto responseData = response.getData();
        assertEquals(NotificationStatus.Pending, responseData.getStatus());
        assertNotNull(responseData.getReservations());
        assertEquals(2, responseData.getReservations().size());
        responseData.getReservations().sort((a, b) -> new CompareToBuilder()
                .append(a.getId(), b.getId())
                .toComparison());
        final MaintenanceReservationDto reservationDto1 = responseData.getReservations().get(0);
        assertNotNull(reservationDto1.getNotification());
        assertEquals(ReservationStatus.Rescheduled, reservationDto1.getStatus());
        final MaintenanceReservationDto reservationDto2 = responseData.getReservations().get(1);
        assertNotNull(reservationDto2.getNotification());
        assertEquals(ReservationStatus.Pending, reservationDto2.getStatus());
    }
}