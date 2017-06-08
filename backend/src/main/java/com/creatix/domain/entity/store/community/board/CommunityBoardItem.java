package com.creatix.domain.entity.store.community.board;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.enums.community.board.CommunityBoardItemType;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Data
@EqualsAndHashCode(of = {"id"})
@Entity
public class CommunityBoardItem implements AttachmentId {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(columnDefinition="TEXT", length = 2048, nullable = false)
    private String description;

    @Column(nullable = false)
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

    @Column
    private Boolean showEmailAddress;

    @Column
    private Boolean showApartmentNumber;

    @OneToMany
    private List<CommunityBoardItemPhoto> photoList;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

}
