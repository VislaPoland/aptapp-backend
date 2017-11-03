package com.creatix.controller.v1.business;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.business.*;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ApplicationFeatureType;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.business.BusinessProfileService;
import com.creatix.util.StringUtils;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@RestController
@RequestMapping(path = {"/api/properties/{propertyId}/businesses", "/api/v1/properties/{propertyId}/businesses"})
@ApiVersion(1.0)
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
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.COMMUNITY_BOARD)
    public DataResponse<List<BusinessProfileDto>> listBusinessProfilesForProperty(@PathVariable Long propertyId) {
        return new DataResponse<>(businessProfileService.listBusinessProfilesForProperty(propertyId)
                .stream()
                .map(bp -> businessMapper.toBusinessProfile(bp))
                .collect(Collectors.toList()));
    }

    /**
     *
     * @param propertyId
     * @param businessCategoryIdList Comma separated list of category ids
     * @return
     */
    @ApiOperation(value = "List business profiles for property and category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/categories/{businessCategoryIdList}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public DataResponse<List<BusinessProfileDto>> listBusinessProfilesForPropertyAndCategory(
            @PathVariable Long propertyId,
            @PathVariable String businessCategoryIdList) {
        List<Long> categoryIdList = StringUtils.splitToLong(businessCategoryIdList, ",");
        return new DataResponse<>(businessProfileService.listBusinessesForPropertyAndCategories(propertyId, categoryIdList)
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
    @RequestMapping(path = "/categories/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public DataResponse<List<BusinessProfileDto>> listBusinessProfilesForPropertyAndCategory(
            @PathVariable Long propertyId,
            @Valid @RequestBody BusinessSearchRequest businessSearchRequest) {
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
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public DataResponse<BusinessProfileDto> getBusinessProfile(@PathVariable("businessProfileId") Long businessProfileId) {
        return new DataResponse<>(
                businessMapper.toBusinessProfile(
                        businessProfileService.getById(businessProfileId)
                )
        );
    }

    @ApiOperation(value = "Creates business profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfileDto> createBusinessProfile(@Valid @RequestBody BusinessProfileCreateRequest request,
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
    @PutMapping(path = "/{businessProfileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfileDto> updateBusinessProfile(@Valid @RequestBody BusinessProfileDto request,
                                                                  @PathVariable("businessProfileId") Long businessProfileId) {
        request.setId(businessProfileId);
        return new DataResponse<>(
                businessMapper.toBusinessProfile(businessProfileService.updateBusinessProfileFromRequest(request))
        );
    }

    @ApiOperation(value = "Delete business profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessProfileId}", method = {RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfileDto> delete(@PathVariable("businessProfileId") Long businessProfileId) {
        return new DataResponse<>(
                businessMapper.toBusinessProfile(businessProfileService.deleteBusinessProfile(businessProfileId))
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
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public void notify(@PathVariable("businessProfileId") Long businessProfileId) {
        businessProfileService.sendNotification(businessProfileId);
    }

    @ApiOperation(value = "Lists business profile carte items")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessProfileId}/carte", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE)
    public DataResponse<List<BusinessProfileCarteItemDto>> listCarte(@PathVariable("businessProfileId") Long businessProfileId) {
        return new DataResponse<>(
                businessProfileService.listCarte(businessProfileId).stream().map(
                        e -> businessMapper.toBusinessProfileCarteItem(e)
                ).collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "Upload business profile photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessProfileId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfileDto> storeBusinessProfilePhotos(@RequestParam MultipartFile[] files, @PathVariable long businessProfileId) throws IOException {
        return new DataResponse<>(
                businessMapper.toBusinessProfile(businessProfileService.storeBusinessProfilePhotos(files, businessProfileId))
        );
    }

    @ApiOperation(value = "Upload business profile photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{businessProfileId}/photos/{photoId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured(feature = ApplicationFeatureType.BUSINESS_PROFILE, value = {AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<BusinessProfilePhotoDto> deleteBusinessProfilePhoto(@PathVariable("photoId") Long photoId,
                                                                            @PathVariable("businessProfileId") Long businessProfileId) throws IOException {
        return new DataResponse<>(
                businessMapper.toBusinessProfilePhoto(businessProfileService.deleteBusinessProfilePhoto(photoId))
        );
    }

}
