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
    @NotNull
    @Column(nullable = false, updatable = false)
    private String number;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Tenant usingTenant;
}
