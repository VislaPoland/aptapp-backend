package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.message.PersonalMessageDeleteStatus;
import com.creatix.domain.enums.message.PersonalMessageStatusType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
@Entity
@Data
public class PersonalMessage {

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

}
