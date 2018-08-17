package com.creatix.controller.v1;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.notification.BusinessProfileNotificationDto;
import com.creatix.domain.dto.notification.CommentNotificationDto;
import com.creatix.domain.dto.notification.CommunityBoardItemUpdatedSubscriberNotificationDto;
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
import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.*;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.RoleSecured;
import com.creatix.service.AttachmentService;
import com.creatix.service.NotificationService;
import com.creatix.service.notification.NotificationsStatistics;
import com.creatix.service.notification.NotificationsStatisticsCreator;
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
@RequestMapping(path = {"/api/notifications", "/api/v1/notifications"})
@ApiVersion(1.0)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationsStatisticsCreator notificationsStatisticsCreator;
    @Autowired
    private Mapper mapper;
    @Autowired
    private AttachmentService attachmentService;


    private Class<? extends NotificationDto> getMappingClass(Class clazz) {
        if (clazz.equals(CommentNotification.class)) {
            return CommentNotificationDto.class;
        } else if (clazz.equals(BusinessProfileNotification.class)) {
            return BusinessProfileNotificationDto.class;
        } else if (clazz.equals(CommunityBoardItemUpdatedSubscriberNotification.class)) {
            return CommunityBoardItemUpdatedSubscriberNotificationDto.class;
        } else {
            return NotificationDto.class;
        }
    }

    //general
    @ApiOperation(value = "Filter notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.NotificationsWithReservation.class)
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security})
    public PageableDataResponse<List<NotificationDto>> getNotifications(
            @RequestParam NotificationRequestType requestType,
            @RequestParam int pageSize,
            @RequestParam(required = false) Long startId,
            @RequestParam(required = false) NotificationStatus[] notificationStatus,
            @RequestParam(required = false) NotificationType[] notificationType,
            @RequestParam(required = false) Long propertyId) {

        return mapper.toPageableDataResponse(notificationService.filterNotifications(requestType, notificationStatus, notificationType, startId, propertyId, pageSize),
                n -> mapper.toNotificationDto(n, this.getMappingClass(n.getClass())));
    }

    @ApiOperation(value = "Get single maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/maintenance/{notificationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance}, feature = ApplicationFeatureType.MAINTENANCE)
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
    @RoleSecured(feature = ApplicationFeatureType.MAINTENANCE)
    public DataResponse<MaintenanceNotificationDto> saveMaintenanceNotification(@Valid @RequestBody CreateMaintenanceNotificationRequest dto) throws IOException, TemplateException {
        MaintenanceNotification n = mapper.fromMaintenanceNotificationRequest(dto);
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.saveMaintenanceNotification(dto.getUnitNumber(), n, dto.getSlotUnitId(), dto.getPropertyId())));
    }

    @ApiOperation(value = "Get all maintenance notifications in date range")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/maintenance/calendar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance}, feature = ApplicationFeatureType.MAINTENANCE)
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
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security}, feature = ApplicationFeatureType.SECURITY)
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
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance}, feature = ApplicationFeatureType.SECURITY)
    public DataResponse<SecurityNotificationDto> saveSecurityNotification(@Valid @RequestBody CreateSecurityNotificationRequest dto) throws IOException, TemplateException {
        SecurityNotification n = mapper.fromSecurityNotificationRequest(dto);
        return new DataResponse<>(mapper.toSecurityNotificationDto(notificationService.saveSecurityNotification(n, dto.getPropertyId())));
    }

    @ApiOperation(value = "Respond to security notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/security/{notificationId}/respond", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Security}, feature = ApplicationFeatureType.SECURITY)
    public DataResponse<SecurityNotificationDto> respondToSecurityNotification(@PathVariable long notificationId, @Valid @RequestBody SecurityNotificationResponseRequest request) throws IOException, TemplateException {
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
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.Maintenance, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager}, feature = ApplicationFeatureType.MAINTENANCE)
    public DataResponse<MaintenanceNotificationDto> respondToMaintenanceNotification(@PathVariable Long notificationId, @Valid @RequestBody MaintenanceNotificationResponseRequest request) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.respondToMaintenanceNotification(notificationId, request)));
    }

    @ApiOperation(value = "Close maintenance notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.NotificationsWithReservation.class)
    @PostMapping(path = "/maintenance/{notificationId}/close", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = AccountRole.Maintenance, feature = ApplicationFeatureType.MAINTENANCE)
    public DataResponse<MaintenanceNotificationDto> closeMaintenanceNotification(@PathVariable Long notificationId) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toMaintenanceNotificationDto(notificationService.closeMaintenanceNotification(notificationId)));
    }

    @ApiOperation(value = "Get single neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/neighborhood/{notificationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.Tenant, AccountRole.SubTenant, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security}, feature = ApplicationFeatureType.NEIGHBORHOOD)
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
    @RoleSecured(feature = ApplicationFeatureType.NEIGHBORHOOD)
    public DataResponse<NeighborhoodNotificationDto> saveNeighborhoodNotification(@Valid @RequestBody CreateNeighborhoodNotificationRequest dto) throws MessageDeliveryException, TemplateException, IOException {
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.saveNeighborhoodNotification(dto.getUnitNumber(), mapper.fromNeighborhoodNotificationRequest(dto))));
    }

    @ApiOperation(value = "Get neighborhood notification statistics")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @GetMapping(path = "/neighborhood/stats", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager}, feature = ApplicationFeatureType.NEIGHBORHOOD)
    public DataResponse<NotificationsStatistics> getNeighborhoodNotificationStats(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime till) throws IOException, TemplateException {
        return new DataResponse<>(notificationsStatisticsCreator.createForTimeRange(from, till));
    }

    @ApiOperation(value = "Respond to escalated neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @PostMapping(path = "/neighborhood/escalated/{notificationId}/respond", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager}, feature = ApplicationFeatureType.NEIGHBORHOOD)
    public DataResponse<NeighborhoodNotificationDto> respondToEscalatedNeighborhoodNotification(@PathVariable long notificationId, @Valid @RequestBody NeighborhoodNotificationResponseRequest request) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toNeighborhoodNotificationDto(notificationService.respondToNeighborhoodNotification(notificationId, request)));
    }

    @ApiOperation(value = "Respond to neighborhood notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/neighborhood/{notificationId}/respond", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = AccountRole.Tenant, feature = ApplicationFeatureType.NEIGHBORHOOD)
    public DataResponse<NeighborhoodNotificationDto> respondToNeighborhoodNotification(@PathVariable long notificationId, @Valid @RequestBody NeighborhoodNotificationResponseRequest request) throws IOException, TemplateException {
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
        return new DataResponse<>(
                mapper.toNotificationDto(
                        attachmentService.storeNotificationPhotos(files, notificationId),
                        NotificationDto.class
                )
        );
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
    public HttpEntity<byte[]> downloadNotificationPhoto(@PathVariable Long notificationId, @PathVariable String fileName) throws IOException {
        final NotificationPhoto photo = attachmentService.getNotificationPhoto(notificationId, fileName);
        final File photoFile = new File(photo.getFilePath());
        final byte[] photoFileData = FileUtils.readFileToByteArray(photoFile);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(Files.probeContentType(photoFile.toPath())));
        headers.setContentLength(photoFileData.length);

        return new HttpEntity<>(photoFileData, headers);
    }
}
