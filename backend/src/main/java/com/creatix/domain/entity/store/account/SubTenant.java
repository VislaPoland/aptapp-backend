package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Address;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.Property;
import com.querydsl.core.annotations.QueryInit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "parent_tenant_id")
})
@BatchSize(size = 40)
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
