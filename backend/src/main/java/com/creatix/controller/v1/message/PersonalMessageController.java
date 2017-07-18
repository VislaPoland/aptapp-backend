package com.creatix.controller.v1.message;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.notification.message.PersonalMessageDto;
import com.creatix.domain.entity.store.notification.PersonalMessage;
import com.creatix.domain.mapper.PersonalMessageMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.PersonalMessageService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 29/05/2017.
 */
@RestController
@RequestMapping(path = "/api/v1/personalMessages")
@ApiVersion(1.0)
public class PersonalMessageController {

    @Autowired
    private PersonalMessageService personalMessageService;
    @Autowired
    private PersonalMessageMapper personalMessageMapper;

    @ApiOperation(value = "List of received personal messages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/received", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<PersonalMessageDto>> listReceivedMessages(
            @RequestParam("offset") Long offset,
            @RequestParam("limit") Long limit) {
    return new DataResponse<>(
            personalMessageService.listReceivedMessagesForCurrentUser(offset, limit)
                    .stream()
                    .map(e -> personalMessageMapper.toPersonalMessage(e))
                    .collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "List of sent personal messages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/sent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<PersonalMessageDto>> listSentMessages(
            @RequestParam("offset") Long offset,
            @RequestParam("limit") Long limit) {
    return new DataResponse<>(
            personalMessageService.listSentMessagesForCurrentUser(offset, limit).stream().map(
                    e -> personalMessageMapper.toPersonalMessage(e)
            ).collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "Send new personal message")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<PersonalMessageDto>> createNewPersonalMessage(@Valid @RequestBody CreatePersonalMessageRequest request) {

        List<PersonalMessage> personalMessageList = null;
        switch (request.getPersonalMessageRequestType()) {
            case TO_TENANT:
                personalMessageList = Collections.singletonList(personalMessageService.sendMessageToTenant(request.getRecipientId(), request.getTitle(), request.getContent()));
                break;
            case TO_PROPERTY_MANAGER:
                personalMessageList = personalMessageService.sendMessageToPropertyManagers(request.getRecipientId(), request.getTitle(), request.getContent());
                break;
        }

        return new DataResponse<>(
                personalMessageList
                        .stream()
                        .map(e -> personalMessageMapper.toPersonalMessage(e))
                        .collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "Get specific personal message")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{messageId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PersonalMessageDto> getPersonalMessage(@PathVariable("messageId") Long messageId) {
        return new DataResponse<>(
                personalMessageMapper.toPersonalMessage(
                        personalMessageService.getMessageById(messageId)
                )
        );
    }

    @ApiOperation(value = "Delete personal message")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PersonalMessageDto> deletePersonalMessage(@PathVariable("messageId") Long messageId) {
        return new DataResponse<>(
                personalMessageMapper.toPersonalMessage(
                        personalMessageService.deleteMessage(messageId)
                )
        );
    }


}
