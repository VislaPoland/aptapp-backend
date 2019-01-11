package com.creatix.controller.v1.notifications;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.controller.exception.AptValidationException;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.notification.reporting.NotificationReportDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGlobalInfoDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGroupByAccountDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ApplicationFeatureType;
import com.creatix.domain.enums.NotificationType;
import com.creatix.security.RoleSecured;
import com.creatix.service.notification.NotificationReportService;
import com.creatix.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.BiFunction;

@RestController
@RequestMapping(path = {"/api/notifications/{propertyId}/reporting", "/api/v1/notifications/{propertyId}/reporting"})
@ApiVersion(1.0)
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden")
})
@RoleSecured(value = {AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance}, feature = ApplicationFeatureType.MAINTENANCE)
public class NotificationReportingController {

    private final NotificationReportService notificationReportService;
    private final DateUtils dateUtils;

    @ApiOperation(value = "Get all Maintenance notifications in date range")
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/maintenance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<List<NotificationReportDto>> getMaintenanceNotificationsInDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime till,
            @PathVariable Long propertyId) throws AptValidationException {
        return getNotificationsInDateRange(from, till, NotificationType.Maintenance, propertyId);
    }

    @ApiOperation(value = "Get all Neighborhood notifications in date range")
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/neighborhood", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<List<NotificationReportDto>> getNeighborhoodNotificationsInDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime till,
            @PathVariable Long propertyId) throws AptValidationException {
        return getNotificationsInDateRange(from, till, NotificationType.Neighborhood, propertyId);
    }

    @ApiOperation(value = "Get all Security notifications in date range")
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/security", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<List<NotificationReportDto>> getSecurityNotificationsInDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime till,
            @PathVariable Long propertyId) throws AptValidationException {
        return getNotificationsInDateRange(from, till, NotificationType.Security, propertyId);
    }

    @ApiOperation(value = "Get global Maintenance report information in date range")
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/maintenance/global", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<NotificationReportGlobalInfoDto> getMaintenanceNotificationsGlobalInfoInDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime till,
            @PathVariable Long propertyId) throws AptValidationException {
        return getResponseAfterDateTimeValidation(from, till, (f, t) -> notificationReportService.getGlobalStatistics(f, t, NotificationType.Maintenance, propertyId));
    }

    @ApiOperation(value = "Get global Maintenance information grouped by technician in date range")
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/maintenance/technician", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<List<NotificationReportGroupByAccountDto>> getMaintenanceNotificationsByTechnicianInDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime till,
            @PathVariable Long propertyId) throws AptValidationException {
        return getResponseAfterDateTimeValidation(from, till, (from1, till1) -> notificationReportService.getMaintenanceReportsGroupedByTechnician(from1, till1, propertyId));
    }

    private DataResponse<List<NotificationReportDto>> getNotificationsInDateRange(OffsetDateTime from, OffsetDateTime till, NotificationType notificationType, Long propertyId) throws AptValidationException {
        return getResponseAfterDateTimeValidation(from, till, (f, t) ->
           notificationReportService.getReportsByRange(f,t, notificationType, propertyId)
        );
    }

    private <E> DataResponse<E> getResponseAfterDateTimeValidation(OffsetDateTime from, OffsetDateTime till, BiFunction<OffsetDateTime, OffsetDateTime, E> notificationReportServiceFragment) throws AptValidationException {
        OffsetDateTime localFrom = from, localTill = till;

        if (from == null && till == null) {
            // get range for current month
            Pair<OffsetDateTime, OffsetDateTime> range = dateUtils.getRangeForCurrentMonth();
            localFrom = range.getLeft();
            localTill = range.getRight();
        }

        dateUtils.assertRange(localFrom, localTill);

        return new DataResponse<>(notificationReportServiceFragment.apply(localFrom, localTill));
    }

}
