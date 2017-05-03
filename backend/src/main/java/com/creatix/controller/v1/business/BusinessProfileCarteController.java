package com.creatix.controller.v1.business;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.business.BusinessProfileCarteItemDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.business.BusinessProfileCarteService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Created by Tomas Michalek on 20/04/2017.
 */
@RestController
@RequestMapping(path = {"/api/properties/{propertyId}/businesses/{businessProfileId}/carte", "/api/v1/properties/{propertyId}/businesses/{businessProfileId}/carte"})
@ApiVersion(1.0)
public class BusinessProfileCarteController {

    @Autowired
    private BusinessProfileCarteService businessProfileCarteService;
    @Autowired
    private BusinessMapper businessMapper;

    @ApiOperation(value = "Add carte item in business profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfileCarteItemDto> createCartItem(
            @RequestBody @Valid BusinessProfileCarteItemDto request,
            @PathVariable("businessProfileId") long businessProfileId) {
        return new DataResponse<>(
                businessMapper.toBusinessProfileCarteItem(
                        businessProfileCarteService.createFromRequest(request, businessProfileId)
                )
        );
    }

    @ApiOperation(value = "Update property", notes = "Update existing carte. This endpoint can only be called by account with Administrator role.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfileCarteItemDto> updateCartItem(@RequestBody @Valid BusinessProfileCarteItemDto request) {
        return new DataResponse<>(
                businessMapper.toBusinessProfileCarteItem(
                        businessProfileCarteService.updateFromRequest(request)
                )
        );
    }

    @ApiOperation(value = "Delete property", notes = "Delete existing business profile cart item.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{businessProfileCartItemId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator})
    public DataResponse<BusinessProfileCarteItemDto> deleteCartItem(@PathVariable Long businessProfileCartItemId) {
        return new DataResponse<>(businessMapper.toBusinessProfileCarteItem(businessProfileCarteService.deleteCarteItem(businessProfileCartItemId)));
    }


    @ApiOperation(value = "Upload notification photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessProfileCartItemId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<BusinessProfileCarteItemDto> storeCartItemPhoto(@RequestParam MultipartFile[] files, @PathVariable long businessProfileCartItemId) throws IOException {
        return new DataResponse<>(
                businessMapper.toBusinessProfileCarteItem(businessProfileCarteService.storeBusinessProfilePhotos(files, businessProfileCartItemId))
        );
    }

}
