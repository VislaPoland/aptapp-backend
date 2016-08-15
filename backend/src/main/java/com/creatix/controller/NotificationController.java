package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.notification.maintenance.CreateMaintenanceNotificationRequest;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationResponseRequest;
import com.creatix.domain.dto.notification.neighborhood.CreateNeighborhoodNotificationRequest;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationDto;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationResponseRequest;
import com.creatix.domain.dto.notification.security.CreateSecurityNotificationRequest;
import com.creatix.domain.dto.notification.security.SecurityNotificationDto;
import com.creatix.domain.dto.notification.security.SecurityNotificationResponseRequest;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import com.creatix.domain.entity.store.notification.NotificationPhoto;
import com.creatix.domain.entity.store.notification.SecurityNotification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationRequestType;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.RoleSecured;
import com.creatix.service.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
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
import java.nio.file.Files;
import java.time.OffsetDateTime;
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
    @JsonView(Views.NotificationsWithReservation.class)
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

    @ApiOperation(value = "Get single maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
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
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/maintenance", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security})
    public DataResponse<MaintenanceNotificationDto> saveMaintenanceNotification(@RequestBody @Valid CreateMaintenanceNotificationRequest dto) throws IOException, TemplateException {
        MaintenanceNotification n = mapper.fromMaintenanceNotificationRequest(dto);
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.saveMaintenanceNotification(dto.getUnitNumber(), n, dto.getSlotUnitId())));
    }

    @ApiOperation(value = "Get all maintenance notifications in date range")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/maintenance/calendar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<List<MaintenanceNotificationDto>> getMaintenanceNotificationsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime till) {
        List<MaintenanceNotificationDto> data = notificationService.getAllMaintenanceNotificationsInDateRange(from, till).stream()
                .map(n -> mapper.toMaintenanceNotificationDto(n))
                .collect(Collectors.toList());
        return new DataResponse<>(data);
    }

    @ApiOperation(value = "Get single security notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
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
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/security", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance})
    public DataResponse<SecurityNotificationDto> saveSecurityNotification(@RequestBody @Valid CreateSecurityNotificationRequest dto) throws IOException, TemplateException {
        SecurityNotification n = mapper.fromSecurityNotificationRequest(dto);
        return new DataResponse<>(mapper.toSecurityNotificationDto(notificationService.saveSecurityNotification(n)));
    }

    @ApiOperation(value = "Respond to security notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/security/{notificationId}/respond", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Security})
    public DataResponse<SecurityNotificationDto> respondToSecurityNotification(@PathVariable long notificationId, @RequestBody @Valid SecurityNotificationResponseRequest request) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toSecurityNotificationDto(notificationService.respondToSecurityNotification(notificationId, request)));
    }

    @ApiOperation(value = "Respond to maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.NotificationsWithReservation.class)
    @RequestMapping(path = "/maintenance/{notificationId}/respond", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Maintenance, AccountRole.Tenant})
    public DataResponse<MaintenanceNotificationDto> respondToMaintenanceNotification(@PathVariable Long notificationId, @RequestBody @Valid MaintenanceNotificationResponseRequest request) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.respondToMaintenanceNotification(notificationId, request)));
    }

    @ApiOperation(value = "Get single neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
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
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/neighborhood", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<NeighborhoodNotificationDto> saveNeighborhoodNotification(@RequestBody @Valid CreateNeighborhoodNotificationRequest dto) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.saveNeighborhoodNotification(dto.getUnitNumber(), mapper.fromNeighborhoodNotificationRequest(dto))));
    }


    @ApiOperation(value = "Respond to neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/neighborhood/{notificationId}/respond", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Tenant})
    public DataResponse<NeighborhoodNotificationDto> respondToNeighborhoodNotification(@PathVariable long notificationId, @RequestBody @Valid NeighborhoodNotificationResponseRequest request) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.respondToNeighborhoodNotification(notificationId, request)));
    }

    @ApiOperation(value = "Upload notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{notificationId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<NotificationDto> storeNotificationPhotos(@RequestParam MultipartFile[] files, @PathVariable long notificationId) throws IOException {
        return new DataResponse<>(mapper.toNotificationDto(notificationService.storeNotificationPhotos(files, notificationId)));
    }

    @ApiOperation(value = "Download notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{notificationId}/photos/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> dowloadNotificationPhoto(@PathVariable Long notificationId, @PathVariable String fileName) throws IOException {
        final NotificationPhoto photo = notificationService.getNotificationPhoto(notificationId, fileName);
        final File photoFile = new File(photo.getFilePath());
        final byte[] photoFileData = FileUtils.readFileToByteArray(photoFile);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(Files.probeContentType(photoFile.toPath())));
        headers.setContentLength(photoFileData.length);

        return new HttpEntity<>(photoFileData, headers);
    }
}
