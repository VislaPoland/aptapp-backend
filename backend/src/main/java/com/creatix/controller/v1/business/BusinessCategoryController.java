package com.creatix.controller.v1.business;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.business.BusinessCategoryDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.business.BusinessProfileService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@RestController
@RequestMapping(path = {"/api/businessCategories", "/api/v1/businessCategories"})
@ApiVersion(1.0)
public class BusinessCategoryController {


    @Autowired
    private BusinessProfileService businessProfileService;
    @Autowired
    private BusinessMapper businessMapper;

    @ApiOperation(value = "List business categories")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<BusinessCategoryDto>> listBusinessCategories() {
        return new DataResponse<>(businessProfileService.listBusinessCategories()
                .stream()
                .map(bc -> businessMapper.toBusinessCategory(bc))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Deletes business category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessCategoryId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(AccountRole.Administrator)
    public DataResponse<BusinessCategoryDto> deleteBusinessCategory(@PathVariable Long businessCategoryId) {
        return new DataResponse<>(
                businessMapper.toBusinessCategory(
                    businessProfileService.deleteBusinessCategory(businessCategoryId)
                )
        );
    }

    @ApiOperation(value = "Creates or updates business category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = {RequestMethod.PUT, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(AccountRole.Administrator)
    public DataResponse<BusinessCategoryDto> createBusinessCategory(@RequestBody BusinessCategoryDto businessCategoryDto) {
        return new DataResponse<>(
                businessMapper.toBusinessCategory(
                    businessProfileService.createOrUpdateBusinessCategory(
                            businessMapper.toBusinessCategory(businessCategoryDto)
                    )
                )
        );
    }



}
