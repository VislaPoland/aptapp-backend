package com.creatix.domain.dao;

import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.account.ManagedEmployee;
import com.creatix.domain.enums.AccountRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.creatix.domain.entity.account.QManagedEmployee.managedEmployee;

@Repository
@Transactional
public class ManagedEmployeeDao extends AbstractAccountDao<ManagedEmployee> {

    public List<ManagedEmployee> findByProperty(Property property) {
        return queryFactory
                .selectFrom(managedEmployee)
                .where(managedEmployee.deletedAt.isNull()
                        .and(managedEmployee.manager.managedProperty.eq(property)))
                .fetch();
    }

}
