package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.message.PersonalMessageDeleteStatus;
import com.creatix.domain.enums.message.PersonalMessageStatusType;
import lombok.Data;

import javax.persistence.*;

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

    @Column
    private String title;

    @Column
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private PersonalMessageStatusType messageStatus;

    @Column
    @Enumerated(EnumType.STRING)
    private PersonalMessageDeleteStatus deleteStatus;

}
