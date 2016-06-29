package com.creatix.domain.entity.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"manager"})
public class Employee extends EmployeeBase {

    @ManyToOne
    private PropertyManager manager;

}
