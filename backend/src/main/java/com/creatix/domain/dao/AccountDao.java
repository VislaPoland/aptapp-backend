package com.creatix.domain.dao;

import com.creatix.domain.entity.Account;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return em.createQuery("FROM Account WHERE primaryEmail = :email", Account.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public Account findByActionToken(String actionToken) {
        final List<Account> results = em.createQuery("FROM Account WHERE actionToken = :actionToken", Account.class)
                .setParameter("actionToken", actionToken)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
