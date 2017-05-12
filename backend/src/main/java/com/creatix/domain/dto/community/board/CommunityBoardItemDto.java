package com.creatix.domain.dto.community.board;

import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.enums.community.board.CommunityBoardItemType;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by kvimbi on 10/05/2017.
 */
@Data
public class CommunityBoardItemDto {

    private Long id;
    private String title;
    private String description;
    private Double price;
    private CommunityBoardCategory category;
    private CommunityBoardItemType communityBoardItemType;
    private CommunityBoardStatusType communityBoardStatus;
    private CommunityBoardItemAuthorDto account;
    private List<CommunityBoardItemPhotoDto> photoList;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
