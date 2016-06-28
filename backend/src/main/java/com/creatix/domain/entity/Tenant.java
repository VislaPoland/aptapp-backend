package com.creatix.domain.entity;

import com.creatix.domain.enums.TenantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, of = "id")
public class Tenant extends Account {
    @Column
    @Enumerated(EnumType.STRING)
    private TenantType type;

    @OneToMany(mappedBy = "usingTenant")
    private Set<ParkingStall> parkingStalls;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;

    @OneToMany(mappedBy = "parentTenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubTenant> subTenants;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Apartment apartment;

    @Transient
    public Address getAddress() {
        return apartment.getProperty().getAddress();
    }
}
