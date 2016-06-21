package com.creatix.domain.dao;

import com.creatix.domain.entity.Account;
import com.creatix.domain.entity.QAccount;
import com.creatix.domain.enums.AccountRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AccountDao extends DaoBase<Account, Long> {

    public List<Account> findByRole(AccountRole role) {
        return queryFactory.selectFrom(QAccount.account)
                .where(QAccount.account.role.eq(role))
                .fetch();
    }

    public List<Account> findAll() {
        return queryFactory.selectFrom(QAccount.account)
                .fetch();
    }

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
