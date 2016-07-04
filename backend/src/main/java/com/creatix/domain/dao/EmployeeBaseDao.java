package com.creatix.domain.dao;

import com.creatix.domain.entity.account.EmployeeBase;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EmployeeBaseDao extends AbstractAccountDao<EmployeeBase> {
}
