package com.creatix.message.template.push.community.board;

import com.creatix.domain.entity.store.community.board.CommunityBoardComment;
import com.creatix.message.template.push.PushMessageTemplate;

/**
 * Created by kvimbi on 15/06/2017.
 */
public class NewCommunityItemCommentReplyTemplate extends PushMessageTemplate {

    private final CommunityBoardComment communityBoardComment;

    public NewCommunityItemCommentReplyTemplate(CommunityBoardComment communityBoardComment) {
        this.communityBoardComment = communityBoardComment;
    }

    @Override
    public String getTemplateName() {
        return "new-community-item-comment-reply";
    }

    public CommunityBoardComment getComment() {
        return communityBoardComment;
    }
}
