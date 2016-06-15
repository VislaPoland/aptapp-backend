package com.creatix.domain.dao;

import com.creatix.domain.entity.Account;
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
        return (Account) em.createQuery("FROM Account WHERE primaryEmail = :email")
                .setParameter("email", email)
                .getSingleResult();
    }

    public Account findByActionToken(String actionToken) {
        return (Account) em.createQuery("FROM Account WHERE actionToken = :actionToken")
                .setParameter("actionToken", actionToken)
                .getSingleResult();
    }
}
