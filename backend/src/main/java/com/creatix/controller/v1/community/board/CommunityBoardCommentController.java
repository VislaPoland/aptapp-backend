package com.creatix.controller.v1.community.board;

import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.community.board.CommunityBoardCommentDto;
import com.creatix.domain.dto.community.board.CommunityBoardCommentEditRequest;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.CommunityBoardMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.community.board.CommunityBoardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 12/05/2017.
 */
@RestController
@RequestMapping(path = "/api/v1/properties/{propertyId}/communityBoards/{boardItemId}/comments")
public class CommunityBoardCommentController {

    @Autowired
    private CommunityBoardService communityBoardService;
    @Autowired
    private CommunityBoardMapper communityBoardMapper;


    @ApiOperation(value = "List all comments for board item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "", method = RequestMethod.GET)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Tenant, AccountRole.SubTenant})
    public DataResponse<List<CommunityBoardCommentDto>> listAllComments(@PathVariable("boardItemId") Long boardItemId) {
        return new DataResponse<>(
                communityBoardService.listCommentsForBoardItem(boardItemId).stream().map(
                        e -> communityBoardMapper.toCommunityBoardComment(e)
                ).collect(Collectors.toList())
        );
    }


    @ApiOperation(value = "Creates new comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "", method = RequestMethod.POST)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Tenant, AccountRole.SubTenant})
    public DataResponse<CommunityBoardCommentDto> createNew(@PathVariable("boardItemId") Long boardItemId, @RequestBody CommunityBoardCommentEditRequest request) {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardComment(
                        communityBoardService.createNewCommentFromRequest(boardItemId, request)
                )
        );
    }

    @ApiOperation(value = "Update existing comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "", method = RequestMethod.PUT)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Tenant, AccountRole.SubTenant})
    public DataResponse<CommunityBoardCommentDto> updateItem(@RequestBody CommunityBoardCommentEditRequest request) {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardComment(
                        communityBoardService.updateCommentFromRequest(request)
                )
        );
    }

    @ApiOperation(value = "Delete existing comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/{commentId}", method = RequestMethod.DELETE)
    @RoleSecured()
    public DataResponse<CommunityBoardCommentDto> deleteComment(@PathVariable("commentId") Long commentId) {
        return new DataResponse<>(
                communityBoardMapper.toCommunityBoardComment(
                        communityBoardService.deleteCommentById(commentId)
                )
        );
    }

}
