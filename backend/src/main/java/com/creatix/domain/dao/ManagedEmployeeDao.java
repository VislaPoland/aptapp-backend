package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.ManagedEmployee;
import com.creatix.domain.entity.store.Property;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.creatix.domain.entity.store.account.QManagedEmployee.managedEmployee;

@Repository
@Transactional
public class ManagedEmployeeDao extends AbstractAccountDao<ManagedEmployee> {

    public List<ManagedEmployee> findByProperty(Property property) {
        return queryFactory
                .selectFrom(managedEmployee)
                .where(managedEmployee.deletedAt.isNull()
                        .and(managedEmployee.manager.managedProperty.eq(property)))
                .orderBy(managedEmployee.firstName.lower().asc())
                .orderBy(managedEmployee.lastName.lower().asc())
                .fetch();
    }

}
