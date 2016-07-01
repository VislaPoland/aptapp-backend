package com.creatix.domain.entity.account;

import com.creatix.domain.entity.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"employees", "managedProperty"})
@ToString(callSuper = true, exclude = {"employees", "managedProperty"})
public class PropertyManager extends EmployeeBase {

    @ManyToOne
    @NotNull
    private Property managedProperty;

    @Column
    private String website;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Employee> employees;

}
