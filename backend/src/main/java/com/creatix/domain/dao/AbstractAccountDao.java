package com.creatix.domain.dao;

import com.creatix.domain.entity.account.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional
public class AbstractAccountDao<T extends Account> extends DaoBase<T, Long> {

    @Override
    public void persist(T account) {
        if ( account.getCreatedAt() == null ) {
            account.setCreatedAt(new Date());
            account.setUpdatedAt(new Date());
        }
        else {
            account.setUpdatedAt(new Date());
        }

        super.persist(account);
    }

}
