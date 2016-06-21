package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.notification.*;
import com.creatix.domain.entity.MaintenanceNotification;
import com.creatix.domain.entity.NeighborhoodNotification;
import com.creatix.domain.entity.Notification;
import com.creatix.domain.enums.AccountRole;
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
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<Map<Integer, List<NotificationDto>>> getNotificationsGroupedByDay(@RequestBody @Valid NotificationsCollectionRequest request) {
        return new DataResponse<>(mapper.toNotificationDtoMap(notificationService.getRelevantInDateRangeGroupedByDayNumber(request.getFrom(), request.getTill())));
    }

    /*
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<List<NotificationDto>> getNotifications(@RequestBody @Valid NotificationsCollectionRequest request) {
        return new DataResponse<>(mapper.toNotificationDtoList(notificationService.getRelevantInDateRange(request.getFrom(), request.getTill())));
    }
    */

    @ApiOperation(value = "Get concrete security notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/security/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security})
    public DataResponse<NotificationDto> getSecurityNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toNotificationDto(notificationService.getSecurityNotification(notificationId)));
    }

    @ApiOperation(value = "Get concrete maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/maintenance/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<MaintenanceNotificationDto> getMaintenanceNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.getMaintenanceNotification(notificationId)));
    }

    @ApiOperation(value = "Get concrete neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/neighborhood/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<NeighborhoodNotificationDto> getNeighborhoodNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.getNeighborhoodNotification(notificationId)));
    }

    @ApiOperation(value = "Create security notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/security", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<NotificationDto> saveSecurityNotification(@RequestBody @Valid CreateNotificationRequest dto) {
        Notification n = mapper.fromNotificationDto(dto);
        return new DataResponse<>(mapper.toNotificationDto(notificationService.saveSecurityNotification(n)));
    }

    @ApiOperation(value = "Create maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/maintenance", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security})
    public DataResponse<MaintenanceNotificationDto> saveMaintenanceNotification(@RequestBody @Valid CreateMaintenanceNotificationRequest dto) {
        MaintenanceNotification n = mapper.fromMaintenanceNotificationDto(dto);
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.saveMaintenanceNotification(dto.getUnitNumber(), n)));
    }

    @ApiOperation(value = "Create neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/neighborhood", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<NeighborhoodNotificationDto> saveNeighborhoodNotification(@RequestBody @Valid CreateNeighborhoodNotificationRequest dto) {
        NeighborhoodNotification n = mapper.fromNeighborhoodNotificationDto(dto);
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.saveNeighborhoodNotification(dto.getUnitNumber(), n)));
    }
}
