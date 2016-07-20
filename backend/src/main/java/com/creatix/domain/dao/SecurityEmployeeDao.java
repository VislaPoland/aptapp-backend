package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.QSecurityEmployee;
import com.creatix.domain.entity.store.account.SecurityEmployee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class SecurityEmployeeDao extends DaoBase<SecurityEmployee, Long> {

    @NotNull
    public List<SecurityEmployee> findByProperty(@NotNull Property property) {
        Objects.requireNonNull(property, "Property is null");

        final QSecurityEmployee employee = QSecurityEmployee.securityEmployee;
        return queryFactory.selectFrom(employee)
                .where(employee.manager.managedProperty.eq(property))
                .fetch();
    }
}
