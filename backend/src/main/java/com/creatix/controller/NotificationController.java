package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.notification.NotificationRequestType;
import com.creatix.domain.dto.notification.maintenance.CreateMaintenanceNotificationRequest;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import com.creatix.domain.dto.notification.neighborhood.CreateNeighborhoodNotificationRequest;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationDto;
import com.creatix.domain.dto.notification.security.CreateSecurityNotificationRequest;
import com.creatix.domain.dto.notification.security.SecurityNotificationDto;
import com.creatix.domain.entity.store.MaintenanceNotification;
import com.creatix.domain.entity.store.NeighborhoodNotification;
import com.creatix.domain.entity.store.NotificationPhoto;
import com.creatix.domain.entity.store.SecurityNotification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.NotificationService;
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
    @ApiOperation(value = "Get relevant notifications in date range")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public PageableDataResponse<List<NotificationDto>> getNotifications(
            @RequestParam(required = false, defaultValue = "0") Long page,
            @RequestParam(required = false, defaultValue = "20") Long size,
            @RequestParam NotificationRequestType type) {
        return mapper.toPageableDataResponse(notificationService.getRelevantNotifications(page, size, type), n -> mapper.toNotificationDto(n));
    }


    @ApiOperation(value = "Get all maintenance notifications in date range")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/maintenance", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<List<MaintenanceNotificationDto>> getMaintenanceNotificationsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date till) {
        List<MaintenanceNotificationDto> data = notificationService.getMaintenanceNotificationsInDateRange(from, till).stream()
                .map(n -> mapper.toMaintenanceNotificationDto(n))
                .collect(Collectors.toList());
        return new DataResponse<>(data);
    }


    //security
    @ApiOperation(value = "Get concrete security notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/security/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @RequestMapping(method = RequestMethod.POST, path = "/security", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<SecurityNotificationDto> saveSecurityNotification(@RequestBody @Valid CreateSecurityNotificationRequest dto) {
        SecurityNotification n = mapper.fromSecurityNotificationRequest(dto);
        return new DataResponse<>(mapper.toSecurityNotificationDto(notificationService.saveSecurityNotification(n)));
    }

    //neighborhood
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

    @ApiOperation(value = "Create neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/neighborhood", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<NeighborhoodNotificationDto> saveNeighborhoodNotification(@RequestBody @Valid CreateNeighborhoodNotificationRequest dto) {
        NeighborhoodNotification n = mapper.fromNeighborhoodNotificationRequest(dto);
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.saveNeighborhoodNotification(dto.getUnitNumber(), n)));
    }


    @ApiOperation(value = "upload notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/{notificationId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<NotificationDto> storeNotificationPhotos(@RequestParam MultipartFile[] files, @PathVariable long notificationId) throws IOException {
        return new DataResponse<>(mapper.toNotificationDto(notificationService.storeNotificationPhotos(files, notificationId)));
    }

    @ApiOperation(value = "download notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/{notificationId}/photos/{photoId}", method = RequestMethod.GET)
    @RoleSecured
    @ResponseBody
    public HttpEntity<byte[]> getFile(@PathVariable Long notificationId, @PathVariable Long photoId) throws IOException {
        final NotificationPhoto file = notificationService.getNotificationPhoto(photoId);
        final byte[] fileData = FileUtils.readFileToByteArray(new File(file.getFilePath()));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(fileData.length);

        return new HttpEntity<>(fileData, headers);
    }
}
