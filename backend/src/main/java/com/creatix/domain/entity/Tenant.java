package com.creatix.domain.entity;

import com.creatix.domain.enums.TenantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Tenant extends Account {
    @Column
    @NotNull
    private String unitNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "usingTenant")
    private Set<ParkingStall> parkingStalls;

    @Column
    @Enumerated(EnumType.STRING)
    private TenantType type;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VehicleInformation> vehicles;
}
