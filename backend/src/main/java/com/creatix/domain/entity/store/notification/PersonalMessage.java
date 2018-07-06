package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.enums.message.PersonalMessageDeleteStatus;
import com.creatix.domain.enums.message.PersonalMessageStatusType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
@Entity
@Data
public class PersonalMessage implements AttachmentId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private Account fromAccount;

    @ManyToOne
    private Account toAccount;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 1024, nullable = false)
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private PersonalMessageStatusType messageStatus;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private PersonalMessageDeleteStatus deleteStatus = PersonalMessageDeleteStatus.NONE;


    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    public PersonalMessageGroup personalMessageGroup;

    @OneToMany(mappedBy = "personalMessage", cascade = {CascadeType.REMOVE})
    private List<PersonalMessagePhoto> personalMessagePhotos;

    @PrePersist
    @PreUpdate
    private void beforeCreateOrUpdate() {
        this.title = StringUtils.trimToEmpty(this.title);
        this.content = StringUtils.trimToEmpty(this.content);
    }

}
