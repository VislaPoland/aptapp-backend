package com.creatix.message.template.push.community.board;

import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.enums.PushNotificationTemplateName;
import com.creatix.message.template.push.PushMessageTemplate;

/**
 * Created by kvimbi on 15/06/2017.
 */
public class CommunityItemUpdatedSubscriberTemplate extends PushMessageTemplate {


    public enum EventType {
        UPDATED,
        DELETED,
        NEW_COMMENT
    }

    private final CommunityBoardItem communityBoardItem;
    private final EventType eventType;

    public CommunityItemUpdatedSubscriberTemplate(CommunityBoardItem communityBoardItem, EventType eventType) {
        this.communityBoardItem = communityBoardItem;
        this.eventType = eventType;
    }

    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.COMMUNITY_ITEM_UPDATE.getValue();
    }

    public CommunityBoardItem getCommunityBoardItem() {
        return communityBoardItem;
    }

    public String getTitle() {
        return "Item update";
    }
}
