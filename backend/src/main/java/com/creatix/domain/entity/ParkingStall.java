package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "number")
public class ParkingStall {
    @Id
    @Column(nullable = false)
    @NotNull
    private String number;

    @ManyToOne
    @JoinColumn
    private Tenant usingTenant;
}
