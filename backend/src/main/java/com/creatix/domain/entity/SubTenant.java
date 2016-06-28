package com.creatix.domain.entity;

import com.creatix.domain.enums.TenantType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@SuppressWarnings("Lombok")
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, of = "id")
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
