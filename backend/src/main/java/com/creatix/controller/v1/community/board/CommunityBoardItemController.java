package com.creatix.controller.v1.community.board;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.community.board.*;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import com.creatix.domain.mapper.CommunityBoardMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.community.board.CommunityBoardService;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 11/05/2017.
 */
@RestController
@RequestMapping(path = "/api/v1/properties/{propertyId}/communityBoards")
@ApiVersion(1.0)
public class CommunityBoardItemController {

    @Autowired
    private CommunityBoardService communityBoardService;
    @Autowired
    private CommunityBoardMapper communityBoardMapper;


    @ApiOperation(value = "Creates new item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "", method = RequestMethod.POST)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Tenant, AccountRole.SubTenant})
    public DataResponse<CommunityBoardItemDto> createNew(@PathVariable("propertyId") Long propertyId, @RequestBody @Valid CommunityBoardItemEditRequest request) {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardItem(
                        communityBoardService.createNewBoardItemFromRequest(propertyId, request)
                )
        );
    }

    @ApiOperation(value = "Update existing item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/{itemId}", method = RequestMethod.PUT)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Tenant, AccountRole.SubTenant})
    public DataResponse<CommunityBoardItemDto> updateItem(@RequestBody @Valid CommunityBoardItemEditRequest request) {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardItem(
                        communityBoardService.updateBoardItemFromRequest(request)
                )
        );
    }

    @ApiOperation(value = "Get existing item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/{itemId}", method = RequestMethod.GET)
    @RoleSecured()
    public DataResponse<CommunityBoardItemDto> getItem(@PathVariable("itemId") Long itemId) {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardItem(
                        communityBoardService.getBoardItemById(itemId)
                )
        );
    }

    @ApiOperation(value = "Delete existing item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/{itemId}", method = RequestMethod.DELETE)
    @RoleSecured()
    public DataResponse<CommunityBoardItemDto> deleteItem(@PathVariable("itemId") Long itemId) {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardItem(
                        communityBoardService.deleteBoardItemById(itemId)
                )
        );
    }


    @ApiOperation(value = "List all community board items")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "", method = RequestMethod.GET)
    @RoleSecured
    public PageableDataResponse<List<CommunityBoardItemDto>> listVisibleItems(@PathVariable("propertyId") Long propertyId,
                                                                                           @RequestParam(value = "startId", required = false) Long startId,
                                                                                           @RequestParam(value = "pageSize", required = true) Long pageSize,
                                                                                           @RequestParam(value = "categoryList", required = false) String categoryList) {
        if (null == categoryList || "".equals(categoryList)) {
            List<CommunityBoardItem> boardItems = communityBoardService.listBoardItemsForProperty(propertyId, Collections.singletonList(CommunityBoardStatusType.OPEN), startId, pageSize + 1);
            return new PageableDataResponse<>(boardItems
                    .stream()
                    .limit(pageSize)
                    .map(e-> communityBoardMapper.toCommunityBoardItem(e))
                    .collect(Collectors.toList()),
                    pageSize, boardItems.size() > pageSize ? boardItems.get(pageSize.intValue()).getId() : null
            );
        } else {
            List<Long> categoryIdList = StringUtils.splitToLong(categoryList, ",");
            List<CommunityBoardItem> boardItems = communityBoardService.listBoardItemsForPropertyAndCategory(propertyId, Collections.singletonList(CommunityBoardStatusType.OPEN), categoryIdList, startId, pageSize + 1);
            return new PageableDataResponse<>(boardItems
                    .stream()
                    .limit(pageSize)
                    .map(e-> communityBoardMapper.toCommunityBoardItem(e))
                    .collect(Collectors.toList()),
                    pageSize, boardItems.size() > pageSize ? boardItems.get(pageSize.intValue()).getId() : null
            );
        }
    }


    @ApiOperation(value = "Search community board items")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    @RoleSecured
    public PageableDataResponse<List<CommunityBoardItemDto>> searchBoard(@PathVariable("propertyId") Long propertyId,
                                                        @RequestBody @Valid SearchRequest searchRequest) {

        List<CommunityBoardItem> boardItems = communityBoardService.searchBoardItemsForProperty(propertyId, Collections.singletonList(CommunityBoardStatusType.OPEN), searchRequest.getPageSize() + 1, searchRequest);
        return new PageableDataResponse<>(boardItems
                .stream()
                .limit(searchRequest.getPageSize())
                .map(e-> communityBoardMapper.toCommunityBoardItem(e))
                .collect(Collectors.toList()),
                searchRequest.getPageSize(), boardItems.size() > searchRequest.getPageSize() ? boardItems.get(searchRequest.getPageSize().intValue()).getId() : null
        );
    }

    @ApiOperation(value = "Upload community item photos")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{itemId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<CommunityBoardItemDto> storeCommunityItemPhotos(@RequestParam MultipartFile[] files, @PathVariable("itemId") long itemId) throws IOException {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardItem(communityBoardService.storeBoardItemPhotos(files, itemId))
        );
    }

    @ApiOperation(value = "Upload community item photos")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{itemId}/photos/{photoId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<CommunityBoardItemPhotoDto> deleteCommunityItemPhoto(@PathVariable("itemId") long itemId,
                                                                             @PathVariable("photoId") long photoId) throws IOException {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardItemPhoto(communityBoardService.deleteCommunityItemPhoto(photoId))
        );
    }


    @ApiOperation(value = "List of categories for community board")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<CommunityBoardCategoryDto>> listCommunityBoardCategories() {
        return new DataResponse<>(
                communityBoardService.listCategories()
                        .stream()
                        .map(category -> communityBoardMapper.toCommunityBoardCategoryDto(category))
                        .collect(Collectors.toList())
        );
    }

}
