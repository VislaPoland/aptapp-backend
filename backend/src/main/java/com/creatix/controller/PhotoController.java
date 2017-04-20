package com.creatix.controller;

import com.creatix.domain.dto.Views;
import com.creatix.service.StoredFilesService;
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
 * Created by kvimbi on 20/04/2017.
 */
@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private StoredFilesService storedFilesService;

    @ApiOperation(value = "Download notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{photoId}/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> downloadPhoto(@PathVariable Long photoId, @PathVariable String fileName) throws IOException {
        StoredFilesService.DownloadPhotoResult photoFileData = storedFilesService.downloadPhoto(photoId, fileName);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(photoFileData.getMediaType());
        headers.setContentLength(photoFileData.getPhotoData().length);

        return new HttpEntity<>(photoFileData.getPhotoData(), headers);
    }

}
