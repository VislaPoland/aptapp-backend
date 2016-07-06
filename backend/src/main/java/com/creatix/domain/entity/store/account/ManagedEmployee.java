package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.account.EmployeeBase;
import com.creatix.domain.entity.store.account.PropertyManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"manager"})
@ToString(callSuper = true, exclude = {"manager"})
public class ManagedEmployee extends EmployeeBase {

    @ManyToOne
    private PropertyManager manager;

}
