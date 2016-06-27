package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.notification.*;
import com.creatix.domain.entity.MaintenanceNotification;
import com.creatix.domain.entity.NeighborhoodNotification;
import com.creatix.domain.entity.NotificationPhoto;
import com.creatix.domain.entity.SecurityNotification;
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
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public DataResponse<List<NotificationDto>> getNotifications(@RequestBody @Valid NotificationsCollectionRequest request) {
        List<NotificationDto> data = notificationService.getRelevantInDateRange(request.getFrom(), request.getTill()).stream()
                .map(n -> mapper.toNotificationDto(n))
                .collect(Collectors.toList());
        return new DataResponse<>(data);
    }

    //maintenance
    @ApiOperation(value = "Get single maintenance notification")
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

    @ApiOperation(value = "Create maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/maintenance", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security})
    public DataResponse<MaintenanceNotificationDto> saveMaintenanceNotification(@RequestBody @Valid CreateMaintenanceNotificationRequest dto) {
        MaintenanceNotification n = mapper.fromMaintenanceNotificationRequest(dto);
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.saveMaintenanceNotification(dto.getUnitNumber(), n)));
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
    public DataResponse<NotificationDto> storeNotificationPhotos(@RequestParam MultipartFile[] files, @PathVariable long notificationId) throws IOException {
        return new DataResponse<>(mapper.toNotificationDto(notificationService.storeNotificationPhotos(files, notificationId)));
    }

    @ApiOperation(value = "download notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/{notificationId}/photos/{photoId}", method = RequestMethod.GET)
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
