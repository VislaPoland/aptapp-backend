package com.creatix.domain.dao;

import com.creatix.domain.entity.account.QEmployee;
import com.creatix.domain.entity.account.Employee;
import com.creatix.domain.enums.AccountRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class EmployeeDao extends AbstractAccountDao<Employee> {

    public List<Employee> findAllByProperty(Long propertyId) {
        final QEmployee employee = QEmployee.employee;
        return queryFactory
                .selectFrom(employee)
                .where(employee.deletedAt.isNull()
                        .and(employee.manager.managedProperty.id.eq(propertyId)))
                .fetch();
    }

    public List<Employee> findAllNotAssistantsByProperty(Long propertyId) {
        final QEmployee employee = QEmployee.employee;
        return queryFactory
                .selectFrom(employee)
                .where(employee.deletedAt.isNull()
                        .and(employee.manager.managedProperty.id.eq(propertyId)
                                .and(employee.role.ne(AccountRole.AssistantPropertyManager))))
                .fetch();
    }

    public List<Employee> findAllAssistantsByProperty(Long propertyId) {
        final QEmployee employee = QEmployee.employee;
        return queryFactory
                .selectFrom(employee)
                .where(employee.deletedAt.isNull()
                        .and(employee.manager.managedProperty.id.eq(propertyId)
                                .and(employee.role.eq(AccountRole.AssistantPropertyManager))))
                .fetch();
    }

}
