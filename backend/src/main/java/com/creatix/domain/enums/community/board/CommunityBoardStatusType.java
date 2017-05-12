package com.creatix.domain.enums.community.board;

/**
 * Created by kvimbi on 11/05/2017.
 */
public enum CommunityBoardStatusType {

    /**
     * Draft listing, visible only to the user who created the item
     */
    DRAFT,
    /**
     * Publicly available
     */
    OPEN,
    /**
     * User can sent private links, not listed publicly
     */
    PRIVATE,
    /**
     * Item is sold, or not valid anymore
     */
    CLOSED,
    /**
     * Deleted item
     */
    DELETED

}
