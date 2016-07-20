package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.MaintenanceEmployee;
import com.creatix.domain.entity.store.account.QMaintenanceEmployee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class MaintenanceEmployeeDao extends DaoBase<MaintenanceEmployee, Long> {

    @NotNull
    public List<MaintenanceEmployee> findByProperty(@NotNull Property property) {
        Objects.requireNonNull(property, "Property is null");

        final QMaintenanceEmployee employee = QMaintenanceEmployee.maintenanceEmployee;
        return queryFactory.selectFrom(employee)
                .where(employee.manager.managedProperty.eq(property))
                .fetch();
    }
}
