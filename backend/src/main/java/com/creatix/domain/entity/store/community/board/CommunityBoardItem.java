package com.creatix.domain.entity.store.community.board;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.enums.community.board.CommunityBoardItemType;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Data
@Entity
public class CommunityBoardItem implements AttachmentId {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition="TEXT")
    private String description;

    @Column
    private Double price;

    @ManyToOne
    private CommunityBoardCategory category;

    @Column
    @Enumerated(EnumType.STRING)
    private CommunityBoardItemType communityBoardItemType;

    @Column
    @Enumerated(EnumType.STRING)
    private CommunityBoardStatusType communityBoardStatus;

    @ManyToOne
    private Property property;

    @ManyToOne
    private Account account;

    @OneToMany
    private List<CommunityBoardItemPhoto> photoList;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (null == id && this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

}
