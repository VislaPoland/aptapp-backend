package com.creatix.domain.entity.store.community.board;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.enums.community.board.CommunityBoardItemType;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
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
    @NotBlank
    private String title;

    @Column(columnDefinition="TEXT", length = 2048, nullable = false)
    @NotBlank
    private String description;

    @Column(nullable = false)
    @Min(0)
    private Double price;

    @ManyToOne
    @NotNull
    private CommunityBoardCategory category;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull
    private CommunityBoardItemType communityBoardItemType;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull
    private CommunityBoardStatusType communityBoardStatus;

    @ManyToOne
    @NotNull
    private Property property;

    @ManyToOne
    @NotNull
    private Account account;

    @Column
    private Boolean showEmailAddress;

    @Column
    private Boolean showApartmentNumber;

    @Column
    private Boolean showPhoneNumber;

    @OneToMany
    private List<CommunityBoardItemPhoto> photoList;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if ( this.createdAt == null ) {
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }
        else {
            this.updatedAt = OffsetDateTime.now();
        }
    }

}
