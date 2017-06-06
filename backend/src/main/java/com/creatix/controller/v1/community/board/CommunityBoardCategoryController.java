package com.creatix.controller.v1.community.board;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.community.board.CommunityBoardCategoryDto;
import com.creatix.domain.mapper.CommunityBoardMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.community.board.CommunityBoardService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kvimbi on 02/06/2017.
 */
@RestController
@RequestMapping(path = "/api/v1/communityBoardCategories")
@ApiVersion(1.0)
public class CommunityBoardCategoryController {

    @Autowired
    private CommunityBoardService communityBoardService;
    @Autowired
    private CommunityBoardMapper communityBoardMapper;

    @ApiOperation(value = "List of categories for community board")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
