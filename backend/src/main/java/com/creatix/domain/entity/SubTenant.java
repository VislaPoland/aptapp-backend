package com.creatix.domain.entity;

import com.creatix.domain.enums.TenantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"parentTenant"})
public class SubTenant extends Account {
    @Column
    @Enumerated(EnumType.STRING)
    private TenantType type;

    @ManyToOne
    private Tenant parentTenant;

    @Transient
    public Apartment getApartment() {
        return parentTenant.getApartment();
    }

    @Transient
    public Property getProperty() {
        return parentTenant.getApartment().getProperty();
    }

    @Transient
    public Address getAddress() {
        return parentTenant.getAddress();
    }
}
