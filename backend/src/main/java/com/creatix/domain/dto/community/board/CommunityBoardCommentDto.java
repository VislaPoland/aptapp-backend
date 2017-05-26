package com.creatix.domain.dto.community.board;

import com.creatix.domain.enums.community.board.CommunityBoardCommentStatusType;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Created by Tomas Michalek on 12/05/2017.
 */
@Data
public class CommunityBoardCommentDto {

    private Long id;
    private String content;
    private CommunityBoardItemAuthorDto author;
    private Long parentCommentId;
    private OffsetDateTime createdAt;
    private CommunityBoardCommentStatusType status;

}
