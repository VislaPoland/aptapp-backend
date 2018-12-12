package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(indexes = {
        @Index(columnList = "manager_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"manager"})
@ToString(callSuper = true, exclude = {"manager"})
public abstract class ManagedEmployee extends EmployeeBase {

    @ManyToOne
    private PropertyManager manager;

    @Override
    public Property getProperty() {
        return manager.getManagedProperty();
    }

}
