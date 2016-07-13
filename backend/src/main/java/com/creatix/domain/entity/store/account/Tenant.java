package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Address;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.ParkingStall;
import com.creatix.domain.entity.store.Vehicle;
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
@EqualsAndHashCode(callSuper = true, exclude = {"vehicles", "subTenants", "apartment"})
@ToString(callSuper = true, exclude = {"vehicles", "subTenants", "apartment"})
public class Tenant extends TenantBase {

    @OneToMany(mappedBy = "usingTenant")
    private Set<ParkingStall> parkingStalls;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;

    @OneToMany(mappedBy = "parentTenant", orphanRemoval = true)
    private Set<SubTenant> subTenants;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Apartment apartment;

    @Column
    @NotNull
    private Boolean enableSms;

    @Transient
    public Address getAddress() {
        return apartment.getProperty().getAddress();
    }
}
