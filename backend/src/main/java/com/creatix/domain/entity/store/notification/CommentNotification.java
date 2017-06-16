package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.community.board.CommunityBoardComment;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by kvimbi on 15/06/2017.
 */
@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentNotification extends Notification {

    @ManyToOne
    private CommunityBoardComment communityBoardComment;


    public CommentNotification() {
        this.type = NotificationType.Comment;
    }

    @Override
    public void setType(NotificationType type) {}

}
