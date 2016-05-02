package com.creatix.domain.dao;

import com.creatix.domain.entity.Account;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class AccountDao extends DaoBase<Account, Long> {

    public Account findById(long id) {
        return em.find(Account.class, id);
    }

    /**
     * Find account by email address. This method will return even deleted accounts
     * to prevent email name clash and user spoofing.
     * @param email unique email address to find account by
     * @return found account
     */
    public Account findByEmail(String email) {
        return (Account) em.createQuery("FROM Account WHERE email = :email")
                .setParameter("email", email)
                .getSingleResult();
    }

}
