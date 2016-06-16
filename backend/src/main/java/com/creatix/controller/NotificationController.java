package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.notification.MaintenanceNotificationDto;
import com.creatix.domain.dto.notification.NeighborhoodNotificationDto;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.notification.RequestNotificationsDto;
import com.creatix.security.RoleSecured;
import com.creatix.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Transactional
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private Mapper mapper;

    @ApiOperation(value = "Get relevant notifications in date range grouped by day number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    //TODO filtering with spring security
    public DataResponse<Map<Integer, List<NotificationDto>>> getNotificationsGroupedByDay(@RequestBody @Valid RequestNotificationsDto request) {
        return new DataResponse<>(mapper.toNotificationDtoMap(notificationService.getAllInDateRangeGroupedByDay(request.getFrom(), request.getTill())));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/security/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    //TODO make accessible only to relevant users
    public DataResponse<NotificationDto> getSecurityNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toNotificationDto(notificationService.getNotification(notificationId)));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/maintenance/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    //TODO make accessible only to relevant users
    public DataResponse<MaintenanceNotificationDto> getMaintenanceNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.getMaintenanceNotification(notificationId)));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/neighborhood/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    //TODO make accessible only to relevant users
    public DataResponse<NeighborhoodNotificationDto> getNeighborhoodNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.getNeighborhoodNotification(notificationId)));
    }
}