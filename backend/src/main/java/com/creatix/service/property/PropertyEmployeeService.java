package com.creatix.service.property;

import com.creatix.domain.dao.EmployeeBaseDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.EmployeeBase;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Service
@Transactional
public class PropertyEmployeeService {
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private EmployeeBaseDao employeeBaseDao;
    @Autowired
    private AuthorizationManager authorizationManager;

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public EmployeeBase delete(@NotNull Long propertyId, @NotNull Long employeeId) {
        Objects.requireNonNull(propertyId);

        final Property property = this.getProperty(propertyId);
        this.authorizationManager.checkRead(property);

        final EmployeeBase employee = this.getEmployee(employeeId);
        if (employee.isDeleted() == false) {
            employee.setDeletedAt(new Date());
            this.employeeBaseDao.persist(employee);
        }

        return employee;
    }

    private Property getProperty(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId);
        final Property property = this.propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }
        return property;
    }

    private EmployeeBase getEmployee(@NotNull Long employeeId) {
        Objects.requireNonNull(employeeId);

        final EmployeeBase contact = this.employeeBaseDao.findById(employeeId);

        if ( contact == null ) {
            throw new EntityNotFoundException(String.format("Employee id=%d not found", employeeId));
        }

        return contact;
    }
}
