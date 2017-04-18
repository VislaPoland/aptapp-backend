package com.creatix.controller.business;

import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.business.BusinessProfileDto;
import com.creatix.domain.dto.business.BusinessSearchRequest;
import com.creatix.domain.dto.business.DiscountCouponDto;
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
 * Created by Tomas Michalek on 12/04/2017.
 */
@RestController
@RequestMapping("/api/properties/{propertyId}/businesses")
public class BusinessProfileController {


    @Autowired
    private BusinessProfileService businessProfileService;
    @Autowired
    private BusinessMapper businessMapper;

    @ApiOperation(value = "List business profiles for property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<BusinessProfileDto>> listBusinessProfilesForProperty(@PathVariable Long propertyId) {
        return new DataResponse<>(businessProfileService.listBusinessProfilesForProperty(propertyId)
                .stream()
                .map(bp -> businessMapper.toBusinessProfile(bp))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "List businesss profiles for property and category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/categories/{businessCategoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<BusinessProfileDto>> listBusinessProfilesForPropertyAndCategory(
            @PathVariable Long propertyId,
            @PathVariable Long businessCategoryId) {
        return new DataResponse<>(businessProfileService.listBusinessesForPropertyAndCategory(propertyId, businessCategoryId)
                .stream()
                .map(bp -> businessMapper.toBusinessProfile(bp))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Search business profiles for property by name and/or category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/categories/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<BusinessProfileDto>> listBusinessProfilesForPropertyAndCategory(
            @PathVariable Long propertyId,
            @RequestBody BusinessSearchRequest businessSearchRequest) {
        return new DataResponse<>(businessProfileService.searchBusinesses(propertyId,
                                    businessSearchRequest.getName(),
                                    businessSearchRequest.getBusinessCategoryId())
                .stream()
                .map(bp -> businessMapper.toBusinessProfile(bp))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get single business profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessProfileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<BusinessProfileDto> getBusinessProfile(@PathVariable("businessProfileId") Long businessProfileId) {
        return new DataResponse<>(
                businessMapper.toBusinessProfile(
                        businessProfileService.getById(businessProfileId)
                )
        );
    }

    @ApiOperation(value = "List business discount coupons")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessProfileId}/discountCoupons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<DiscountCouponDto>> listDiscountCoupons(@PathVariable("businessProfileId") Long businessProfileId) {
        return new DataResponse<>(
                businessProfileService.listBusinessDiscountCoupons(businessProfileId)
                        .stream()
                        .map(dc -> businessMapper.toDiscountCoupon(dc))
                        .collect(Collectors.toList())
        );
    }


    @ApiOperation(value = "Creates business profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfileDto> createBusinessProfile(@RequestBody BusinessProfileDto request,
                                                                  @PathVariable("propertyId") Long propertyId) {
        return new DataResponse<>(
                businessMapper.toBusinessProfile(
                        businessProfileService.createBusinessProfileFromRequest(request, propertyId)
                )
        );
    }

    @ApiOperation(value = "Updates business profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfileDto> update(@RequestBody BusinessProfileDto request) {
        return new DataResponse<>(
                businessMapper.toBusinessProfile(businessProfileService.updateBusinessProfileFromRequest(request))
        );
    }

    @ApiOperation(value = "Sends push notification about new business profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessProfileId}/sendNotification", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public void notify(@PathVariable("businessProfileId") Long businessProfileId) {
        businessProfileService.sendNotification(businessProfileId);
    }


}
