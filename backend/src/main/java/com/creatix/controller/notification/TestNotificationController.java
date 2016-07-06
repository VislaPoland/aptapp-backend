package com.creatix.controller.notification;

import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.entity.push.notification.PushNotification;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.PushNotificationSenderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@Transactional
@RequestMapping("/api/test/notifications")
public class TestNotificationController {

    @Autowired
    private PushNotificationSenderService notificationSenderService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private AuthorizationManager authorizationManager;

    @ApiOperation(value = "Sending test notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<Void> getNotifications(@RequestParam(required = false, defaultValue = "TEST") String message) {
        Object deviceObject = httpSession.getAttribute("device");
        if (deviceObject instanceof Device == false) {
            throw new SecurityException("Device is not recognized.");
        }
        Device device = (Device) deviceObject;
        if (device.getPushToken() == null) {
            throw new SecurityException("Device has not assigned token.");
        }

        PushNotification notification = new PushNotification();
        notification.setTitle("Test");
        notification.setMessage(message);
        notificationSenderService.sendNotification(notification, device);
        return new DataResponse<>();
    }

}
