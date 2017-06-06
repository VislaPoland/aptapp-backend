package com.creatix.domain.dto.community.board;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Tomas Michalek on 11/05/2017.
 */
@ApiModel
@Data
public class SearchRequest {

    private String title;
    private String description;
    private Long communityBoardCategoryId;

    private Boolean orderDesc = true;
    private Long startId = null;
    private Long pageSize = 50L;
    private SearchRequestOrderColumn orderBy = SearchRequestOrderColumn.CREATED;

}