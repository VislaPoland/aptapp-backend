package com.creatix.controller.v1;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.Views;
import com.creatix.service.AttachmentService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by Tomas Michalek on 20/04/2017.
 */
@RestController
@RequestMapping(path = {"/api/attachments", "/api/v1/attachments"})
@ApiVersion(1.0)
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @ApiOperation(value = "Download notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{attachmentId}/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> downloadPhoto(@PathVariable Long attachmentId, @PathVariable String fileName) throws IOException {
        AttachmentService.DownloadAttachment attachmentFileData = attachmentService.downloadAttachment(attachmentId, fileName);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(attachmentFileData.getMediaType());
        headers.setContentLength(attachmentFileData.getFileContent().length);

        return new HttpEntity<>(attachmentFileData.getFileContent(), headers);
    }

}
