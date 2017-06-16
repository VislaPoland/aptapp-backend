package com.creatix.message.template.push;

import com.creatix.domain.entity.store.community.board.CommunityBoardComment;

/**
 * Created by kvimbi on 15/06/2017.
 */
public class NewCommunityItemCommentTemplate extends PushMessageTemplate {

    private final CommunityBoardComment communityBoardComment;

    public NewCommunityItemCommentTemplate(CommunityBoardComment communityBoardComment) {
        this.communityBoardComment = communityBoardComment;
    }

    @Override
    public String getTemplateName() {
        return "new-community-item-comment";
    }

    public CommunityBoardComment getComment() {
        return communityBoardComment;
    }
}
