package com.creatix.domain.dto.community.board;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

/**
 * Created by Tomas Michalek on 11/05/2017.
 */

@Data
@ApiModel
public class SearchRequest {

    @ApiModelProperty
    private String title;

    @ApiModelProperty
    private String description;

    @ApiModelProperty
    private Long communityBoardCategoryId;

    @ApiModelProperty
    private Boolean orderDesc = true;

    @ApiModelProperty
    private Long startId = null;

    @Min(1)
    @Max(1000)
    @ApiModelProperty
    private Long pageSize = 50L;

    @ApiModelProperty
    @NotNull
    private SearchRequestOrderColumn orderBy = SearchRequestOrderColumn.CREATED;

}