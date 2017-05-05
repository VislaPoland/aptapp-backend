package com.creatix.controller;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.ApplicationFeatureDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.ApplicationFeatureService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@RestController
@RequestMapping(path = {"/api/properties/{propertyId}/features", "/api/v1/properties/{propertyId}/feature"})
@ApiVersion(1.0)
public class ApplicationFeatureController {


    @Autowired
    private ApplicationFeatureService applicationFeatureService;
    @Autowired
    private Mapper mapper;

    @ApiOperation(value = "Update application feature configuration")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(path = "/{applicationFeatureId}/{status}", method = RequestMethod.PUT)
    @JsonView(Views.Public.class)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<ApplicationFeatureDto> persistFeaturesForProperty(@PathVariable Long applicationFeatureId, @PathVariable boolean status) {
        return new DataResponse<>(
                mapper.toApplicationFeatureDto(applicationFeatureService.updateFeatureStatus(applicationFeatureId, status))
        );
    }


}
