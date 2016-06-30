package com.creatix.domain.entity;

import com.creatix.domain.entity.account.Tenant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "licensePlate")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String licensePlate;

    @Column(nullable = false)
    @NotNull
    private String make;

    @Column(nullable = false)
    @NotNull
    private String model;

    @Column(nullable = false)
    @NotNull
    private Integer year;

    @Column(nullable = false)
    @NotNull
    private String color;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Tenant owner;

    @OneToOne(mappedBy = "parkingVehicle", optional = false)
    private ParkingStall parkingStall;
}
