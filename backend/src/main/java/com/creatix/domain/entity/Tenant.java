package com.creatix.domain.entity;

import com.creatix.domain.enums.TenantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Tenant extends Account {
    @Column
    @Enumerated(EnumType.STRING)
    private TenantType type;

    @OneToOne(optional = false)
    @JoinColumn
    private Address address;

    @OneToMany(mappedBy = "usingTenant")
    private Set<ParkingStall> parkingStalls;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;

    @OneToMany(mappedBy = "parentTenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubTenant> subTenants;

    @OneToOne(optional = false)
    @JoinColumn
    private Apartment apartment;

    @OneToOne
    @JoinColumn
    private Tenant aboveTenant;

    @OneToOne
    @JoinColumn
    private Tenant belowTenant;

    @OneToOne
    @JoinColumn
    private Tenant leftTenant;

    @OneToOne
    @JoinColumn
    private Tenant rightTenant;

    @OneToOne
    @JoinColumn
    private Tenant oppositeTenant;

    @OneToOne
    @JoinColumn
    private Tenant behindTenant;
}
