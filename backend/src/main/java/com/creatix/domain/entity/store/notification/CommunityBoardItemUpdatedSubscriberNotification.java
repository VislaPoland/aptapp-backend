package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by kvimbi on 29/06/2017.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class CommunityBoardItemUpdatedSubscriberNotification extends Notification {

    @ManyToOne
    private CommunityBoardItem communityBoardItem;

    public CommunityBoardItemUpdatedSubscriberNotification() {
        this.type = NotificationType.CommunityBoardItemUpdatedSubscriber;
    }

    @Override
    public void setType(NotificationType type) {}

}
