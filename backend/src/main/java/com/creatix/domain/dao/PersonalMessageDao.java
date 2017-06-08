package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.notification.PersonalMessage;
import com.creatix.domain.enums.message.PersonalMessageDeleteStatus;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import static com.creatix.domain.entity.store.notification.QPersonalMessage.personalMessage;

import java.util.List;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
@Repository
@Transactional
public class PersonalMessageDao extends DaoBase<PersonalMessage, Long> {

    public List<PersonalMessage> listUserReceivedMessage(Account toAccount, long offset, long limit) {
        return queryFactory
                .selectFrom(personalMessage)
                .where(personalMessage.toAccount.eq(toAccount).and(
                            personalMessage.deleteStatus.ne(PersonalMessageDeleteStatus.BOTH).and(
                                    personalMessage.deleteStatus.ne(PersonalMessageDeleteStatus.RECIPIENT)
                            ).or(personalMessage.deleteStatus.isNull())
                        )
                )
                .offset(offset)
                .limit(limit)
                .fetch();
    }
    public List<PersonalMessage> listUserSentMessage(Account fromAccount, long offset, long limit) {
        return queryFactory
                .selectFrom(personalMessage)
                .where(personalMessage.fromAccount.eq(fromAccount).and(
                            personalMessage.deleteStatus.ne(PersonalMessageDeleteStatus.BOTH).and(
                                personalMessage.deleteStatus.ne(PersonalMessageDeleteStatus.SENDER)
                            )
                        ).or(personalMessage.deleteStatus.isNull())
                )
                .offset(offset)
                .limit(limit)
                .fetch();
    }

}
