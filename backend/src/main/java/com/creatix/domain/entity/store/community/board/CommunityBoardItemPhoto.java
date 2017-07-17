package com.creatix.domain.entity.store.community.board;

import com.creatix.domain.entity.store.attachment.AttachedEntityType;
import com.creatix.domain.entity.store.attachment.Attachment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityBoardItemPhoto extends Attachment {

    @OneToOne
    private CommunityBoardItem communityBoardItem;

    public CommunityBoardItemPhoto() {
        this.attachedEntityType = AttachedEntityType.COMMUNITY_BOARD_ITEM;
    }

    @Override
    public void setAttachedEntityType(AttachedEntityType attachedEntityType) {}

}
