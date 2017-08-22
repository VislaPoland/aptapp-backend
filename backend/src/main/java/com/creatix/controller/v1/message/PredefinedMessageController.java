package com.creatix.controller.v1.message;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.property.message.CreatePredefinedMessageRequest;
import com.creatix.domain.dto.property.message.PredefinedMessageDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.PredefinedMessageService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tomas Sedlak on 21.8.2017.
 */
@Controller
@RequestMapping(path = {"/api/properties/{propertyId}/messages/predefined", "/api/v1/properties/{propertyId}/messages/predefined"})
@ApiVersion(1.0)
public class PredefinedMessageController {

    @Autowired
    private Mapper mapper;
    @Autowired
    private PredefinedMessageService predefinedMessageService;

    @ApiOperation(value = "List predefined messages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<PredefinedMessageDto>> getPredefinedMessages(@NotNull @PathVariable Long propertyId) {
        return new DataResponse<>(predefinedMessageService.getPredefinedMessages(propertyId).stream()
                .map(pm -> mapper.toPredefinedMessageDto(pm))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Create new predefined message for property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public HttpEntity<PredefinedMessageDto> createPredefinedMessage(@NotNull @PathVariable Long propertyId, @RequestBody @NotNull @Valid CreatePredefinedMessageRequest req) {
        return new HttpEntity<>(mapper.toPredefinedMessageDto(predefinedMessageService.createFromRequest(req, propertyId)));
    }

    @ApiOperation(value = "Update existing predefined message for property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @PutMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public HttpEntity<PredefinedMessageDto> updatePredefinedMessage(@RequestBody @NotNull @Valid PredefinedMessageDto messageDto) {
        return new HttpEntity<>(mapper.toPredefinedMessageDto(predefinedMessageService.updateFromRequest(messageDto)));
    }

    @ApiOperation(value = "Delete existing predefined message for property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @DeleteMapping(path = "/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public HttpEntity<PredefinedMessageDto> deletePredefinedMessage(@NotNull @PathVariable Long messageId) {
        return new HttpEntity<>(mapper.toPredefinedMessageDto(predefinedMessageService.deleteById(messageId)));
    }
}
