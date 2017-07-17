package com.creatix.domain.entity.store.community.board;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.community.board.CommunityBoardCommentStatusType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by Tomas Michalek on 12/05/2017.
 */
@Data
@Entity
public class CommunityBoardComment {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String content;

    @ManyToOne
    private Account author;

    @ManyToOne(optional = true)
    private CommunityBoardComment parentComment;

    @OneToMany
    private List<CommunityBoardComment> childComments;

    @ManyToOne(optional = false)
    @NotNull
    private CommunityBoardItem communityBoardItem;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @Column
    @Enumerated(EnumType.STRING)
    private CommunityBoardCommentStatusType status;

    @PrePersist
    public void prePersist() {
        if (null == id && null == this.createdAt) {
            this.createdAt = OffsetDateTime.now();
        }
    }

}
