package com.creatix.controller.v1.community.board;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.community.board.CommunityBoardItemDto;
import com.creatix.domain.dto.community.board.SearchRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import com.creatix.domain.mapper.CommunityBoardMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.community.board.CommunityBoardService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kvimbi on 11/05/2017.
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
    public DataResponse<CommunityBoardItemDto> createNew(@PathVariable("propertyId") Long propertyId, @RequestBody CommunityBoardItemDto request) {
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
    @RequestMapping(path = "", method = RequestMethod.PUT)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Tenant, AccountRole.SubTenant})
    public DataResponse<CommunityBoardItemDto> updateItem(@RequestBody CommunityBoardItemDto request) {
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
    @RequestMapping(path = "/{page}/{limit}", method = RequestMethod.GET)
    @RoleSecured
    public DataResponse<List<CommunityBoardItemDto>> listVisibleItems(@PathVariable("propertyId") Long propertyId,
                                                                      @PathVariable("page") Long offset,
                                                                      @PathVariable("limit") Long limit) {
        return new DataResponse<>(communityBoardService.listBoardItemsForProperty(propertyId, offset, limit)
                .stream()
                .filter(e -> e.getCommunityBoardStatus() == CommunityBoardStatusType.OPEN)
                .map(e-> communityBoardMapper.toCommunityBoardItem(e))
                .collect(Collectors.toList())
        );
    }


    @ApiOperation(value = "Search community board items")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    @RoleSecured
    public DataResponse<List<CommunityBoardItemDto>> searchBoard(@PathVariable("propertyId") Long propertyId,
                                                        @RequestBody SearchRequest searchRequest) {
        return new DataResponse<>(communityBoardService.searchBoardItemsForProperty(propertyId, searchRequest)
                .stream()
                .filter(e -> e.getCommunityBoardStatus() == CommunityBoardStatusType.OPEN)
                .map(e-> communityBoardMapper.toCommunityBoardItem(e))
                .collect(Collectors.toList())
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

}
