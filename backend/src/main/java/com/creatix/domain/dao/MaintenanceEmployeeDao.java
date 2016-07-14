package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.MaintenanceEmployee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MaintenanceEmployeeDao extends DaoBase<MaintenanceEmployee, Long> {
}
