package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.*;
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
    private Set<ParkingStall> parkingStalls = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles = new HashSet<>();

    @OneToMany(mappedBy = "parentTenant", orphanRemoval = true)
    private Set<SubTenant> subTenants = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Apartment apartment;

    @NotNull
    @Column
    private Boolean enableSms;

    @Transient
    public Address getAddress() {
        return apartment.getProperty().getAddress();
    }

    @Transient
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public Property getProperty() {
        return apartment != null ? apartment.getProperty() : null;
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
