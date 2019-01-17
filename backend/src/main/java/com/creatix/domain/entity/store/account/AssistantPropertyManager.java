package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AssistantPropertyManager extends EmployeeBase {

    @ManyToOne
    private PropertyManager manager;

    @Override
    public Property getProperty() {
        return manager.getManagedProperty();
    }
}
