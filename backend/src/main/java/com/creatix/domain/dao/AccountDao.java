package com.creatix.domain.dao;

import com.creatix.domain.entity.Account;
import com.creatix.domain.entity.QAccount;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AccountDao extends DaoBase<Account, Long> {

    /**
     * Find account by primaryEmail address. This method will return even deleted accounts
     * to prevent primaryEmail name clash and user spoofing.
     *
     * @param email unique primaryEmail address to find account by
     * @return found account
     */
    public Account findByEmail(String email) {
        QAccount account = QAccount.account;
        return queryFactory.selectFrom(account)
                .where(account.primaryEmail.eq(email))
                .fetchOne();
    }

    public Account findByActionToken(String actionToken) {
        QAccount account = QAccount.account;
        return queryFactory.selectFrom(account)
                .where(account.actionToken.eq(actionToken))
                .fetchOne();
    }
}
