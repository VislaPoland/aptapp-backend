package com.creatix.domain.entity.account;

import com.creatix.domain.entity.Address;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Property;
import com.querydsl.core.annotations.QueryInit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"parentTenant"})
public class SubTenant extends TenantBase {

    @QueryInit("apartment.property")
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
