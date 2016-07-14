package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.SecurityEmployee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SecurityEmployeeDao extends DaoBase<SecurityEmployee, Long> {
}
