package com.creatix.domain.dto.community.board;

import lombok.Data;

/**
 * Created by kvimbi on 02/06/2017.
 */
@Data
public class CommunityBoardCommentEditRequest {

    private Long id;
    private String content;
    private Long parentCommentId;

}
