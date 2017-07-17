package com.creatix.domain.dto.community.board;

import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.enums.community.board.CommunityBoardItemType;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import lombok.Data;

/**
 * Created by kvimbi on 02/06/2017.
 */
@Data
public class CommunityBoardItemEditRequest {

    private Long id;
    private String title;
    private String description;
    private Double price;
    private CommunityBoardCategory category;
    private CommunityBoardItemType communityBoardItemType;
    private CommunityBoardStatusType communityBoardStatus;
    private PrivacySettingsDto privacySettings;

}
