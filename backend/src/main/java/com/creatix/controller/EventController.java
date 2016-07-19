package com.creatix.controller;


import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.property.slot.EventSlotDto;
import com.creatix.domain.dto.property.slot.PersistEventSlotRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.SlotService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping("/api/properties/{propertyId}/events")
public class EventController {

    @Autowired
    private SlotService slotService;
    @Autowired
    private Mapper mapper;


    @ApiOperation(value = "Get events", notes = "Get all events for single proerty where event begin time falls within desired time span.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
    @RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<EventSlotDto> createEventSlot(@PathVariable Long propertyId, @RequestBody @Valid PersistEventSlotRequest request) {
        return new DataResponse<>(mapper.toEventSlotDto(slotService.createEventSlot(propertyId, request)));
    }

    @ApiOperation(value = "Delete event slot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{eventSlotId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<EventSlotDto> deleteEventSlot(@PathVariable Long eventSlotId) {
        return new DataResponse<>(mapper.toEventSlotDto(slotService.deleteEventSlotById(eventSlotId)));
    }

}
