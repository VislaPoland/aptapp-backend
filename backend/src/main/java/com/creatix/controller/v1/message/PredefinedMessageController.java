package com.creatix.controller.v1.message;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.property.message.CreatePredefinedMessageRequest;
import com.creatix.domain.dto.property.message.PredefinedMessageDto;
import com.creatix.domain.entity.store.PredefinedMessagePhoto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.message.PredefinedMessageService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tomas Sedlak on 21.8.2017.
 */
@RestController
@Transactional
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

    @ApiOperation(value = "Upload predefined message photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{predefinedMessageId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PredefinedMessageDto> storePredefinedMessagePhotos(@RequestParam MultipartFile[] files, @PathVariable long predefinedMessageId) throws IOException {
        return new DataResponse<>(
                mapper.toPredefinedMessageDto(predefinedMessageService.storePredefinedMessagePhotos(files, predefinedMessageId))
        );
    }

    @ApiOperation(value = "Download predefined message photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{predefinedMessageId}/photos/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    @RoleSecured
    public HttpEntity<byte[]> downloadPredefinedMessagePhoto(@PathVariable Long predefinedMessageId, @PathVariable String fileName) throws IOException {
        final PredefinedMessagePhoto photo = predefinedMessageService.getPredefinedMessagePhoto(predefinedMessageId, fileName);
        final File photoFile = new File(photo.getFilePath());
        final byte[] photoFileData = FileUtils.readFileToByteArray(photoFile);

        final HttpHeaders headers = new HttpHeaders();
        if (photoFile.toPath().toString().toUpperCase().endsWith(".JPEG")) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        } else if (photoFile.toPath().toString().toUpperCase().endsWith(".GIF")) {
            headers.setContentType(MediaType.IMAGE_GIF);
        } else {
            headers.setContentType(MediaType.IMAGE_PNG);
        }
        headers.setContentLength(photoFileData.length);

        return new HttpEntity<>(photoFileData, headers);
    }
}
