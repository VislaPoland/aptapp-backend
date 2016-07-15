package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.enums.NotificationRequestType;
import com.creatix.domain.dto.notification.maintenance.CreateMaintenanceNotificationRequest;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import com.creatix.domain.dto.notification.neighborhood.CreateNeighborhoodNotificationRequest;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationDto;
import com.creatix.domain.dto.notification.security.CreateSecurityNotificationRequest;
import com.creatix.domain.dto.notification.security.SecurityNotificationDto;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.entity.store.notification.NotificationPhoto;
import com.creatix.domain.entity.store.notification.SecurityNotification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.RoleSecured;
import com.creatix.service.NotificationService;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private Mapper mapper;

    //general
    @ApiOperation(value = "Filter notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public PageableDataResponse<List<NotificationDto>> getNotifications(
            @RequestParam NotificationRequestType requestType,
            @RequestParam int pageSize,
            @RequestParam(required = false) Long startId,
            @RequestParam(required = false) NotificationStatus notificationStatus,
            @RequestParam(required = false) NotificationType notificationType) {
        return mapper.toPageableDataResponse(notificationService.filterNotifications(requestType, notificationStatus, notificationType, startId, pageSize), n -> mapper.toNotificationDto(n));
    }

    //maintenance
    @ApiOperation(value = "Filter maintenance notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/maintenance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public PageableDataResponse<List<MaintenanceNotificationDto>> getMaintenanceNotifications(
            @RequestParam(required = false, defaultValue = "0") Long page,
            @RequestParam(required = false, defaultValue = "20") Long size,
            @RequestParam NotificationRequestType type,
            @RequestParam(required = false) NotificationStatus status) {
        return mapper.toPageableDataResponse(notificationService.filterMaintenanceNotifications(type, status, page, size), n -> mapper.toMaintenanceNotificationDto(n));
    }

    @ApiOperation(value = "Get single maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/maintenance/{notificationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<MaintenanceNotificationDto> getMaintenanceNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.getMaintenanceNotification(notificationId)));
    }

    @ApiOperation(value = "Create maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/maintenance", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security})
    public DataResponse<MaintenanceNotificationDto> saveMaintenanceNotification(@RequestBody @Valid CreateMaintenanceNotificationRequest dto) {
        MaintenanceNotification n = mapper.fromMaintenanceNotificationRequest(dto);
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.saveMaintenanceNotification(dto.getUnitNumber(), n)));
    }

    @ApiOperation(value = "Get all maintenance notifications in date range")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/maintenance/calendar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<List<MaintenanceNotificationDto>> getMaintenanceNotificationsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date till) {
        List<MaintenanceNotificationDto> data = notificationService.getAllMaintenanceNotificationsInDateRange(from, till).stream()
                .map(n -> mapper.toMaintenanceNotificationDto(n))
                .collect(Collectors.toList());
        return new DataResponse<>(data);
    }

    //security
    @ApiOperation(value = "Filter security notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/security", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public PageableDataResponse<List<SecurityNotificationDto>> getSecurityNotifications(
            @RequestParam(required = false, defaultValue = "0") Long page,
            @RequestParam(required = false, defaultValue = "20") Long size,
            @RequestParam NotificationRequestType type,
            @RequestParam NotificationStatus status) {
        return mapper.toPageableDataResponse(notificationService.filterSecurityNotifications(type, status, page, size), n -> mapper.toSecurityNotificationDto(n));
    }

    @ApiOperation(value = "Get single security notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/security/{notificationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security})
    public DataResponse<SecurityNotificationDto> getSecurityNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toSecurityNotificationDto(notificationService.getSecurityNotification(notificationId)));
    }

    @ApiOperation(value = "Create security notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/security", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<SecurityNotificationDto> saveSecurityNotification(@RequestBody @Valid CreateSecurityNotificationRequest dto) {
        SecurityNotification n = mapper.fromSecurityNotificationRequest(dto);
        return new DataResponse<>(mapper.toSecurityNotificationDto(notificationService.saveSecurityNotification(n)));
    }

    //neighborhood
    @ApiOperation(value = "Filter neighborhood notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/neighborhood", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public PageableDataResponse<List<NeighborhoodNotificationDto>> getNeighborhoodNotifications(
            @RequestParam(required = false, defaultValue = "0") Long page,
            @RequestParam(required = false, defaultValue = "20") Long size,
            @RequestParam NotificationRequestType type,
            @RequestParam NotificationStatus status) {
        return mapper.toPageableDataResponse(notificationService.filterNeighborhoodNotifications(type, status, page, size), n -> mapper.toNeighborhoodNotificationDto(n));
    }

    @ApiOperation(value = "Get single neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/neighborhood/{notificationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<NeighborhoodNotificationDto> getNeighborhoodNotificationDetail(@PathVariable Long notificationId) {
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.getNeighborhoodNotification(notificationId)));
    }

    @ApiOperation(value = "Create neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/neighborhood", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<NeighborhoodNotificationDto> saveNeighborhoodNotification(@RequestBody @Valid CreateNeighborhoodNotificationRequest dto) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.saveNeighborhoodNotification(dto.getUnitNumber(), mapper.fromNeighborhoodNotificationRequest(dto))));
    }


    @ApiOperation(value = "Upload notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(path = "/{notificationId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<NotificationDto> storeNotificationPhotos(@RequestParam MultipartFile[] files, @PathVariable long notificationId) throws IOException {
        return new DataResponse<>(mapper.toNotificationDto(notificationService.storeNotificationPhotos(files, notificationId)));
    }

    @ApiOperation(value = "Download notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/{notificationId}/photos/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> dowloadNotificationPhoto(@PathVariable Long notificationId, @PathVariable String fileName) throws IOException {
        final NotificationPhoto file = notificationService.getNotificationPhoto(notificationId, fileName);
        final byte[] fileData = FileUtils.readFileToByteArray(new File(file.getFilePath()));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(fileData.length);

        return new HttpEntity<>(fileData, headers);
    }
}
