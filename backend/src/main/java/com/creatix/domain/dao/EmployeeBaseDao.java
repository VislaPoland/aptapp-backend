package com.creatix.domain.dao;

import com.creatix.domain.entity.account.Employee;
import com.creatix.domain.entity.account.EmployeeBase;
import com.creatix.domain.entity.account.QEmployee;
import com.creatix.domain.entity.account.QPropertyManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EmployeeBaseDao extends AbstractAccountDao<EmployeeBase> {

    public EmployeeBase findById(Long propertyId, Long employeeId) {
        EmployeeBase employeeBase = null;

        final QEmployee employee = QEmployee.employee;
        employeeBase = queryFactory
                .selectFrom(employee)
                .where(employee.manager.managedProperty.id.eq(propertyId).and(employee.id.eq(employeeId)))
                .fetchFirst();

        if (employeeBase != null) {
            return employeeBase;
        }

        final QPropertyManager propertyManager = QPropertyManager.propertyManager;
        employeeBase = queryFactory
                .select(propertyManager)
                .where(propertyManager.managedProperty.id.eq(propertyId).and(propertyManager.id.eq(employeeId)))
                .fetchFirst();

        return employeeBase;
    }

}
