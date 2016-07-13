package com.creatix.domain.entity.store.account;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"manager"})
@ToString(callSuper = true, exclude = {"manager"})
public class Employee extends EmployeeBase {

    @ManyToOne
    private PropertyManager manager;

}
