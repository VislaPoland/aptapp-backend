package com.creatix.domain.entity.account;

import com.creatix.domain.entity.Address;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.ParkingStall;
import com.creatix.domain.entity.Vehicle;
import com.creatix.domain.entity.account.Account;
import com.creatix.domain.entity.account.SubTenant;
import com.creatix.domain.enums.TenantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"vehicles", "subTenants", "apartment"})
public class Tenant extends Account {
    @Column
    @Enumerated(EnumType.STRING)
    private TenantType type;

    @OneToMany(mappedBy = "usingTenant")
    private Set<ParkingStall> parkingStalls;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;

    @OneToMany(mappedBy = "parentTenant", orphanRemoval = true)
    private Set<SubTenant> subTenants;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Apartment apartment;

    @Transient
    public Address getAddress() {
        return apartment.getProperty().getAddress();
    }
}
