package com.creatix.controller.v1;


import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.property.slot.EventSlotDetailDto;
import com.creatix.domain.dto.property.slot.EventSlotDto;
import com.creatix.domain.dto.property.slot.PersistEventSlotRequest;
import com.creatix.domain.dto.property.slot.UpdateEventSlotRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ApplicationFeatureType;
import com.creatix.domain.enums.EventInviteResponse;
import com.creatix.security.RoleSecured;
import com.creatix.service.SlotService;
import com.fasterxml.jackson.annotation.JsonView;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@ApiVersion(1.0)
public class EventController {

    @Autowired
    private SlotService slotService;
    @Autowired
    private Mapper mapper;


    @ApiOperation(value = "Get events", notes = "Get all events for single property where event begin time falls within desired time span.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @GetMapping(path = {"/api/properties/{propertyId}/events", "/api/v1/properties/{propertyId}/events"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<EventSlotDto>> getEvents(
            @PathVariable Long propertyId,
            @ApiParam(example = "2016-07-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginDate,
            @ApiParam(example = "2016-07-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return new DataResponse<>(slotService.getEventSlotsByPropertyIdAndTimeRange(propertyId, beginDate, endDate).stream()
                .map(e -> mapper.toEventSlotDto(e))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Create event slot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @PostMapping(path = {"/api/properties/{propertyId}/events", "/api/v1/properties/{propertyId}/events"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<EventSlotDto> createEventSlot(@PathVariable Long propertyId, @Valid @RequestBody PersistEventSlotRequest request) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toEventSlotDto(slotService.createEventSlot(propertyId, request)));
    }

    @ApiOperation(value = "Get event detail", notes = "Get event detail including attendants list with their responses based on privilege.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @GetMapping(path = {"/api/properties/{propertyId}/events/{eventSlotId}", "/api/v1/events/{eventSlotId}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<EventSlotDetailDto> getEventDetailWithFilteredAttendants(@PathVariable Long eventSlotId) {
        return new DataResponse<>(slotService.getEventDetail(eventSlotId));
    }

    @ApiOperation(value = "Respond to event invitation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @PutMapping(path = "/api/v1/events/{eventSlotId}/response", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<EventSlotDetailDto> respondToEventInvite(@PathVariable Long eventSlotId, @RequestParam EventInviteResponse response) {
        slotService.respondToEventInvite(eventSlotId, response);
        return new DataResponse<>(slotService.getEventDetail(eventSlotId));
    }

    @ApiOperation(value = "Update event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @PutMapping(path = "/api/v1/events/{eventSlotId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<EventSlotDto> updateEvent(@PathVariable Long eventSlotId, @Valid @RequestBody UpdateEventSlotRequest data) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toEventSlotDto(slotService.updateEventSlot(eventSlotId, data)));
    }

    @ApiOperation(value = "Delete event slot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @DeleteMapping(path = {"/api/properties/{propertyId}/events/{eventSlotId}", "/api/v1/events/{eventSlotId}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<EventSlotDto> deleteEventSlot(@PathVariable Long eventSlotId) throws IOException, TemplateException {
        return new DataResponse<>(mapper.toEventSlotDto(slotService.deleteEventSlotById(eventSlotId)));
    }

    @ApiOperation(value = "Upload event slot photoa")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/api/v1/events/{eventSlotId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<EventSlotDto> storeBusinessProfilePhotos(@RequestParam MultipartFile[] files, @PathVariable long eventSlotId) {
        return new DataResponse<>(
                mapper.toEventSlotDto(slotService.storeEventSlotPhotos(files, eventSlotId))
        );
    }

}
