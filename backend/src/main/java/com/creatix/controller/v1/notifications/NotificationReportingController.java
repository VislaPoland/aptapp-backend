package com.creatix.controller.v1.notifications;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.controller.exception.AptValidationException;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ApplicationFeatureType;
import com.creatix.security.RoleSecured;
import com.creatix.service.NotificationService;
import com.creatix.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = {"/api/notifications/reporting", "/api/v1/notifications/reporting"})
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

    private final Mapper mapper;
    private final NotificationService notificationService;
    private final DateUtils dateUtils;

    @ApiOperation(value = "Get all maintenance notifications in date range")
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/maintenance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<List<MaintenanceNotificationDto>> getMaintenanceNotificationsInDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime till) throws AptValidationException {

        OffsetDateTime localFrom = from, localTill = till;

        if (from == null && till == null) {
            // get range for current month
            Pair<OffsetDateTime, OffsetDateTime> range = dateUtils.getRangeForCurrentMonth();
            localFrom = range.getLeft();
            localTill = range.getRight();
        }

        dateUtils.assertRange(localFrom, localTill);

        List<MaintenanceNotificationDto> data = notificationService.getAllMaintenanceNotificationsInDateRange(localFrom, localTill).stream()
                .map(mapper::toMaintenanceNotificationDto)
                .collect(Collectors.toList());
        return new DataResponse<>(data);
    }
}
