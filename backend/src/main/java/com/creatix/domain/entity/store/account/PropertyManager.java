package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@BatchSize(size = 40)
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
    private Set<ManagedEmployee> employees;

}
