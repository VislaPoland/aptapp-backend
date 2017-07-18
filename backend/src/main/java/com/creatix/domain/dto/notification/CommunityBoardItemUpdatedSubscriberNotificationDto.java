package com.creatix.domain.dto.notification;

import com.creatix.domain.dto.community.board.CommunityBoardCommentDto;
import com.creatix.domain.dto.community.board.CommunityBoardItemDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Tomas Michalek on 30/06/2017.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityBoardItemUpdatedSubscriberNotificationDto extends NotificationDto {

    private CommunityBoardItemDto communityBoardItem;

    private CommunityBoardCommentDto communityBoardComment;

}
