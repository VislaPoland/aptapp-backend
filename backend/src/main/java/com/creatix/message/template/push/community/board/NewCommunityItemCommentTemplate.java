package com.creatix.message.template.push.community.board;

import com.creatix.domain.entity.store.community.board.CommunityBoardComment;
import com.creatix.domain.enums.PushNotificationTemplateName;
import com.creatix.message.template.push.PushMessageTemplate;

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
        return PushNotificationTemplateName.COMMUNITY_ITEM_COMMENT.getValue();
    }

    public CommunityBoardComment getComment() {
        return communityBoardComment;
    }
}
