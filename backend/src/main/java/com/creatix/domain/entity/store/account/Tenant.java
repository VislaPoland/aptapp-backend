package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Address;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.ParkingStall;
import com.creatix.domain.entity.store.Vehicle;
import com.creatix.domain.enums.TenantType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(indexes = {
        @Index(columnList = "apartment_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"vehicles", "subTenants", "apartment"})
@ToString(callSuper = true, exclude = {"vehicles", "subTenants", "apartment", "parkingStalls"})
public class Tenant extends TenantBase {

    @OneToMany(mappedBy = "usingTenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParkingStall> parkingStalls;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;

    @OneToMany(mappedBy = "parentTenant", orphanRemoval = true)
    private Set<SubTenant> subTenants;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Apartment apartment;

    @NotNull
    @Column
    private Boolean enableSms;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column
    private TenantType type;

    @Transient
    public Address getAddress() {
        return apartment.getProperty().getAddress();
    }

    public void addVehicle(@NotNull Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "Vehicle is null");
        if ( vehicles == null ) {
            vehicles = new HashSet<>();
        }

        vehicles.add(vehicle);
        vehicle.setOwner(this);
    }

    public boolean removeVehicle(@NotNull Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "Vehicle is null");
        if ( vehicles == null ) {
            return false;
        }

        final boolean remove = vehicles.remove(vehicle);
        if ( remove ) {
            vehicle.setOwner(null);
        }

        return remove;
    }

    public void addParkingStall(@NotNull ParkingStall parkingStall) {
        Objects.requireNonNull(parkingStall, "Parking stall is null");
        if ( parkingStalls == null ) {
            parkingStalls = new HashSet<>();
        }

        parkingStalls.add(parkingStall);
        parkingStall.setUsingTenant(this);
    }

    public boolean removeParkingStall(@NotNull ParkingStall parkingStall) {
        Objects.requireNonNull(parkingStall, "Parking stall is null");
        if ( parkingStalls == null ) {
            return false;
        }

        final boolean remove = parkingStalls.remove(parkingStall);
        if ( remove ) {
            parkingStall.setUsingTenant(null);
        }

        return remove;
    }
}
